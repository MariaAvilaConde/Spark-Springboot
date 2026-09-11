package com.universidad.sparkdb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    /**
     * GET / → muestra los endpoints disponibles en lugar de 404
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("app", "Spark + Spring Boot — Multi-DB JOIN");
        info.put("status", "running");
        info.put("endpoints", Map.of(
                "join",            "GET /api/spark/join",
                "stats",           "GET /api/spark/stats",
                "sin_matricula",   "GET /api/spark/sin-matricula"
        ));
        return ResponseEntity.ok(info);
    }
}
