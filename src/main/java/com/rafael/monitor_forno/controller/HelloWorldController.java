package com.rafael.monitor_forno.controller;

import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.service.HelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/hello-world")
public class HelloWorldController {

    @Autowired
    private HelloWorldService helloWorldService;

    @GetMapping
    public String helloWorld() {
        return helloWorldService.helloWorld("Rafael");
    }

    @PostMapping("/{id}")
    public String helloWorldPost(@PathVariable String id, @RequestParam(value = "filter", defaultValue = "nenhum") String filter, @RequestBody Sessao body) {
        return "Hello World " + filter;
    }
}
