package com.example.edgeservice.controller;

import com.example.edgeservice.model.FilledGrapeWine;
import com.example.edgeservice.model.Grape;
import com.example.edgeservice.model.Wine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class FilledGrapeWineController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${wineservice.baseurl}")
    private String wineServiceBaseUrl;

    @Value("${grapeservice.baseurl}")
    private String grapeServiceBaseUrl;

    @GetMapping("/combo/wine/{name}")
    public FilledGrapeWine getGrapeByWine(@PathVariable String name){
        Wine wine = restTemplate.getForObject("http://" + wineServiceBaseUrl + "/wines/name/{name}" + name,
                Wine.class);

        Grape grape = restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                        Grape.class, wine.getGrapeName());
        return new FilledGrapeWine(grape, wine);
    }

    @GetMapping("/combo/wines")
    public List<FilledGrapeWine> getWinesAndGrapes(){
        List<FilledGrapeWine> returnList = new ArrayList<>();

        ResponseEntity<List<Wine>> responseEntityWines =
                restTemplate.exchange("http://" + wineServiceBaseUrl + "/wines",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Wine>>() {
                        });

        List<Wine> wines = responseEntityWines.getBody();
        for (Wine wine: wines) {
            Grape grape =
                    restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                            Grape.class, wine.getGrapeName());
            returnList.add(new FilledGrapeWine(grape, wine));
        }
        return returnList;
    }

    @GetMapping("/combo/region/{region}")
    public List<FilledGrapeWine> getWinesAndGrapesByRegion(@PathVariable String region){
        List<FilledGrapeWine> returnList = new ArrayList<>();

        ResponseEntity<List<Wine>> responseEntityWines =
                restTemplate.exchange("http://" + wineServiceBaseUrl + "/wines/region/{region}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Wine>>() {
                        }, region);

        List<Wine> wines = responseEntityWines.getBody();
        for (Wine wine: wines) {
            Grape grape =
                    restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                            Grape.class, wine.getGrapeName());
            returnList.add(new FilledGrapeWine(grape, wine));
        }
        return returnList;
    }

    @GetMapping("/combo/country/{country}")
    public List<FilledGrapeWine> getWinesAndGrapesByCountry(@PathVariable String country){
        List<FilledGrapeWine> returnList = new ArrayList<>();

        ResponseEntity<List<Wine>> responseEntityWines =
                restTemplate.exchange("http://" + wineServiceBaseUrl + "/wines/country/{country}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Wine>>() {
                        }, country);
        List<Wine> wines = responseEntityWines.getBody();
        for (Wine wine: wines) {
            Grape grape =
                    restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                            Grape.class, wine.getGrapeName());
            returnList.add(new FilledGrapeWine(grape, wine));
        }
        return returnList;
    }

    @PostMapping("/combo")
    public FilledGrapeWine addWine(@RequestParam String name, @RequestParam String region,
                                   @RequestParam String country, @RequestParam double score, @RequestParam String grapeName) {
        Wine wine =
                restTemplate.postForObject("http://" + wineServiceBaseUrl + "/wines",
                        new Wine(name, region, country, score, grapeName), Wine.class);

        Grape grape = restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                Grape.class, grapeName);

        return new FilledGrapeWine(grape, wine);
    }

    @PutMapping("/combo")
    public FilledGrapeWine updateScore(@RequestParam String name, @RequestParam double score){
        Wine wine = restTemplate.getForObject("http://" + wineServiceBaseUrl + "/wines/name/{name}" + name,
                Wine.class);
        wine.setScore(score);

        ResponseEntity<Wine> responseEntityWine =
                restTemplate.exchange("http://" + wineServiceBaseUrl + "/wines", HttpMethod.PUT, new HttpEntity<>(wine), Wine.class);

        Wine retrievedWine = responseEntityWine.getBody();

        Grape grape =
                restTemplate.getForObject("http://" + grapeServiceBaseUrl + "/grapes/grapename/{grapeName}",
                        Grape.class, retrievedWine.getGrapeName());

        return new FilledGrapeWine(grape, retrievedWine);

    }

    @DeleteMapping("/combo/{name}")
    public ResponseEntity deleteWine(@PathVariable String name){
        restTemplate.delete("http://" + wineServiceBaseUrl + "/wines/name/" + name);

        return ResponseEntity.ok().build();
    }
}
