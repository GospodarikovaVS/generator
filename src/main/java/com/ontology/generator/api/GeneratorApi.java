package com.ontology.generator.api;

import com.ontology.generator.service.GeneratorService;
import com.ontology.generator.service.GraphDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generator")
public class GeneratorApi {
    @Autowired
    private GeneratorService generatorService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }

    @PostMapping("/hello")
    public Boolean helloAdd(@RequestBody final String hello) {
        return true;
    }

    @GetMapping("/getRequests")
    public ResponseEntity<Object> getRequests(@Nullable @RequestParam("classFilter") String classFilter) {
        if (classFilter != null && !classFilter.isBlank()) {
            return ResponseEntity.ok(generatorService.getRequests());
        } else {
            return ResponseEntity.ok(generatorService.getRequests(classFilter));
        }
    }
}
