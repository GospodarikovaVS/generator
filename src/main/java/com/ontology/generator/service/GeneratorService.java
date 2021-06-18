package com.ontology.generator.service;

import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeneratorService {
    @Autowired
    private GraphDBService graphDBService;

    @Value("${graph.db.base.url}")
    private String BASE_URL;

    //// Main service methods
    public Map<String, ArrayList<String>> getRequests() {
        Map<String, ArrayList<String>> result = new HashMap<>();

        // get entities (get classes + get requests for every class)
        ArrayList<String> classesNames = getAllClasses();
        classesNames.forEach(className -> {
            result.putAll(getRequests(className));
        });

        return result;
    }

    public Map<String, ArrayList<String>> getRequests(String classFilter) {
        return getRequests(classFilter, false);
    }

    public Map<String, ArrayList<String>> getRequests(String classFilter, Boolean duplicationChecking) {
        Map<String, ArrayList<String>> result = new HashMap<>();

        //get all predicates
        ArrayList<String> predicates = getAllPossiblePredicatesByClass(classFilter);

        // get entities
        ArrayList<BindingSet> set = getEntitiesByClass(classFilter);
        Map<String, Map<String, String>> entities = new HashMap<>(); // object -> {predicate, object}
        set.forEach(s -> {
                String entityName = s.getValue("o").stringValue().replace(BASE_URL, "");
                result.put(entityName, new ArrayList<>());
                Map<String, String> map = getAllPredicatesByEntity(entityName);
                entities.put(entityName, map);
        });

        //get request for every entity
        result.forEach((obj, reqs) -> {
            if (!duplicationChecking) {
                reqs.addAll(getRequestsForIncompletenessByEntity(entities.get(obj), predicates));
                if (reqs.isEmpty()) {
                    reqs.addAll(getRequestsForDuplicationByEntity(obj, entities.get(obj), entities));
                }
            } else {
                reqs.addAll(getRequestsForDuplicationByEntity(obj, entities.get(obj), entities));
            }
        });

        return result;
    }

    //// Internal service methods
    // get names of classes
    private ArrayList<String> getAllClasses() {
        ArrayList<String> classesNames = new ArrayList<>();
        String query = graphDBService
                .replaceBaseClassInRequest(SparqlQuery.GET_ALL_CLASSES.getQuery());
        graphDBService.sendRequest(query).forEach(b -> {
            classesNames.add(b.getValue("o").stringValue().replace(BASE_URL, ""));
        });
        return classesNames;
    }

    // get entities
    private ArrayList<BindingSet> getAllEntities() {
        String query = graphDBService
                .replaceBaseClassInRequest(SparqlQuery.GET_CONCRETE_CLASS_ENTITIES.getQuery(), "_className_");
        return graphDBService.sendRequest(query);
    }

    private ArrayList<BindingSet> getEntitiesByClass(String className) {
        return graphDBService.sendRequest(SparqlQuery.GET_CONCRETE_CLASS_ENTITIES.getQuery()
                .replace("_className_", className));
    }

    // get predicates
    private ArrayList<String> getAllPossiblePredicatesByClass(String className) {
        ArrayList<String> predicates = new ArrayList<>();
        graphDBService.sendRequest(SparqlQuery.GET_ALL_DOMAIN_PREDICATES.getQuery()
                .replace("_className_", className))
                .forEach(b -> predicates.add(b.getValue("p").stringValue().replace(BASE_URL, "")));
        return predicates;
    }

    private Map<String, String> getAllPredicatesByEntity(String entityName) {
        Map<String, String> predicates = new HashMap<>();
        // TODO: add deleting meta predicates
        graphDBService.sendRequest(SparqlQuery.GET_ALL_PREDICATES_FOR_ENTITIES.getQuery()
                .replace("_entityName_", entityName))
                .forEach(b -> predicates.put(
                        b.getValue("p").stringValue().replace(BASE_URL, ""),
                        b.getValue("s").stringValue().replace(BASE_URL, "")));
        return predicates;
    }

    // get requests
    private ArrayList<String> getRequestsForIncompletenessByEntity(Map<String, String> connections,
                                                                   ArrayList<String> predicates) {
        ArrayList<String> requests = new ArrayList<>();
        predicates.forEach(p -> {
            if (!connections.containsKey(p)) {
                if (requests.isEmpty()) {
                    requests.add("INCOMPLETENESS");
                }
                ArrayList<BindingSet> predLabels = graphDBService.sendRequest(SparqlQuery.GET_LABEL_FOR_PREDICATE
                        .getQuery().replace("_predicateName_", p));
                String predLabel = predLabels.isEmpty() ? p : predLabels.get(0).getValue("o").stringValue();
                requests.add("Необходимо проверить наличие информацию по отношению " + predLabel);
            }
        });
        return requests;
    }

    private ArrayList<String> getRequestsForDuplicationByEntity(String entityName,
                                                                Map<String, String> connections,
                                                                Map<String, Map<String, String>> entities) {
        ArrayList<String> requests = new ArrayList<>();
        entities.forEach((n, e) -> {
            boolean equality = (n != entityName);
            Object[] keys = e.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String p = keys[i].toString();
                String o = e.get(p);
                equality = (equality && o.equals(connections.get(p)));
            }

            if (equality) {
                if (requests.isEmpty()) {
                    requests.add("DUPLICATION");
                }
                requests.add("Проверить на дублирование объект с именем " + n);
            }
        });
        return requests;
    }
}
