package com.ontology.generator.service;

import java.util.HashMap;
import java.util.Map;

public enum SparqlQuery {
    REQUEST_PREFIXES (0L, "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX :<http://www.semanticweb.org/krysanovs/ontologies/2018/5/gazpromneft_demo#> "),

    GET_ALL_CLASSES(1L, (REQUEST_PREFIXES.getQuery() +
            "select * where {  ?o rdfs:subClassOf :_placeForBaseClass_ . } ")),
    GET_CONCRETE_CLASS_ENTITIES(2L, (REQUEST_PREFIXES.getQuery() +
            "select * where {  ?o rdf:type :_className_ . } ")),
    GET_ALL_POSSIBLE_PREDICATES(3L, REQUEST_PREFIXES.getQuery() +
            "select distinct ?p where { ?s ?p ?o . ?s rdf:type :_className_ . filter(?p != rdf:type)}");

    private static final Map<Long, SparqlQuery> identityMap = new HashMap<>();


    static {
        for (SparqlQuery member : values()) {
            identityMap.put(member.getId(), member);
        }
    }

    private final Long id;
    private final String query;

    SparqlQuery(Long id, String query) {
        this.id = id;
        this.query = query;
    }

    public Long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }
}
