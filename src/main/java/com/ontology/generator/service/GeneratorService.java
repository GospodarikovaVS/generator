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
        Map<String, ArrayList<String>> result = new HashMap<>();

        // get entities
        ArrayList<BindingSet> set = getEntitiesByClass(classFilter);
        set.forEach(s -> result.put(s.getValue("o").stringValue(), new ArrayList<>()));
        //get all predicates

        //get request for every entity

        return result;
    }

    private ArrayList<String> getAllClasses() {
        ArrayList<String> classesNames = new ArrayList<>();
        String query = graphDBService
                .replaceBaseClassInRequest(SparqlQuery.GET_ALL_CLASSES.getQuery());
        graphDBService.sendRequest(query).forEach(b -> {
            classesNames.add(b.getValue("o").stringValue().replace(BASE_URL, ""));
        });
        return classesNames;
    }

    private ArrayList<BindingSet> getAllEntities() {
        String query = graphDBService
                .replaceBaseClassInRequest(SparqlQuery.GET_CONCRETE_CLASS_ENTITIES.getQuery(), "_className_");
        return graphDBService.sendRequest(query);
    }

    private ArrayList<BindingSet> getEntitiesByClass(String className) {
        return graphDBService.sendRequest(SparqlQuery.GET_CONCRETE_CLASS_ENTITIES.getQuery()
                .replace("_className_", className));
    }
}
