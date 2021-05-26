package com.ontology.generator.service;

import java.util.HashMap;
import java.util.Map;

public enum SparqlRequest {
    REQUEST_PREFIXES (0L, "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX :<http://www.semanticweb.org/krysanovs/ontologies/2018/5/gazpromneft_demo#> "),
    GET_ALL_CLASSES(1L, (REQUEST_PREFIXES.getRequest() +
            "select * where {  ?s rdfs:subClassOf :placeForBaseClass. } "));

    private static final Map<Long, SparqlRequest> identityMap = new HashMap<>();


    static {
        for (SparqlRequest member : values()) {
            identityMap.put(member.getId(), member);
        }
    }

    private final Long id;
    private final String request;

    SparqlRequest(Long id, String request) {
        this.id = id;
        this.request = request;
    }

    public Long getId() {
        return id;
    }

    public String getRequest() {
        return request;
    }
}
