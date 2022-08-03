package com.mrgrd56.javalearningspringboot.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;

@RestController
public class ProxyController {
    @RequestMapping("/proxy")
    public ResponseEntity<byte[]> proxy(@RequestParam String url, HttpMethod method, HttpEntity<?> httpEntity) {
        return new RestTemplate().exchange(
                url, method, httpEntity, byte[].class
        );
    }
}
