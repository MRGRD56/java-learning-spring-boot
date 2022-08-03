package com.mrgrd56.javalearningspringboot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrgrd56.javalearningspringboot.domain.b.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class AppService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final ConcurrencyService concurrencyService;
    private final HashMapsService hashMapsService;
    private final MultithreadingService multithreadingService;
    private final CollectionsService collectionsService;

    public AppService(
            ObjectMapper objectMapper,
            ConcurrencyService concurrencyService,
            HashMapsService hashMapsService,
            MultithreadingService multithreadingService,
            CollectionsService collectionsService) {
        this.objectMapper = objectMapper;
        this.concurrencyService = concurrencyService;
        this.hashMapsService = hashMapsService;
        this.multithreadingService = multithreadingService;
        this.collectionsService = collectionsService;
    }

    public void start() throws Exception {
        collectionsService.start();
//        var map = new HashMap<String, String>();
//        map.put(null, "ничего");
//        map.put("12", "twelve");
//        map.put("13", "thirteen");
//        map.put("10", "ten");
//        map.put("13", "thirteenager");
//        var mapTableField = map.getClass().getDeclaredField("table");
//        mapTableField.setAccessible(true);
//        var mapTable = mapTableField.get(map);
    }
}
