package com.ontology.generator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeneratorService {
    @Autowired
    private GraphDBService graphDBService;

    public Map<String, String> getRequests() {

    }

    public Map<String, String> getRequests(String classFilter) {

    }
}
