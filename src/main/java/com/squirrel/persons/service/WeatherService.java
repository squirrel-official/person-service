package com.squirrel.persons.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    public RestTemplate restTemplate;

    public void getLocation(){
        ResponseEntity<String> response
                = restTemplate.getForEntity("https://ipinfo.io", String.class);
       System.out.println();
    }
}
