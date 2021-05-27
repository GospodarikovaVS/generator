package com.ontology.generator.api;

import com.ontology.generator.service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

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
    public ResponseEntity<Object> getRequests() {
        return ResponseEntity.ok(generatorService.getRequests());
    }

    @GetMapping("/getRequestsByClass")
    public ResponseEntity<Object> getRequests(@NonNull @RequestParam("classFilter") String classFilter) {
        return ResponseEntity.ok(generatorService.getRequests(classFilter));
    }
}
