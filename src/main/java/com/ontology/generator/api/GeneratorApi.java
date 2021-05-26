package com.ontology.generator.api;

import com.ontology.generator.services.GraphDBService;
import com.ontology.generator.services.HttpClientService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/generator")
public class GeneratorApi {
    @Autowired
    private GraphDBService graphDBService;

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
        ArrayList<Object> result = new ArrayList<>();
        String strRes = graphDBService.sendRequest();
        return ResponseEntity.ok(strRes);
    }
}
