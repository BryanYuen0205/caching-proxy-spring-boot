package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CachingProxyApplication {

    @GetMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/bye")
    public String bye(){
        return "bye";
    }

    @GetMapping("/foos")
    public String getFoos(@RequestParam String id) {
        return "ID: " + id;
    }

    @GetMapping("/api/employeeswithvariable/{id}")
    public String getEmployeesByIdWithVariableName(@PathVariable("id") String employeeId) {
        return "ID: " + employeeId;
    }
}