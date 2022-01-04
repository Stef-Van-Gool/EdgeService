package com.example.edgeservice.controller;

import com.example.edgeservice.model.FilledGrapeWine;
import com.example.edgeservice.model.Grape;
import com.example.edgeservice.model.Wine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

public class FilledGrapeWineController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${wineservice.baseurl}")
    private String wineServiceBaseUrl;

    @Value("${grapeservice.baseurl}")
    private String grapeServiceBaseUrl;

    @GetMapping("/wines/{grapeName}")
    public FilledGrapeWine getWinesByGrape(@PathVariable String grapeName){
        Grape grape = restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                Grape.class, grapeName);

        Wine wine = restTemplate.getForObject("http://" + wineServiceBaseUrl + "/wines/grape/" + grapeName,
                Wine.class);

        return new FilledGrapeWine(grape, wine);
    }
}
