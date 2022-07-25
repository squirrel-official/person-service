package com.squirrel.persons.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    public RestTemplate restTemplate;

    public void getLocation(){
        Map<String, String> map = new RestTemplate().getForObject("https://ipinfo.io", HashMap.class);
        String cityLocation = map.get("city");
        String location = map.get("loc");

    }
}
