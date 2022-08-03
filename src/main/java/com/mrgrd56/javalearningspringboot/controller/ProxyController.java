package com.mrgrd56.javalearningspringboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;

@RestController
public class ImageController {
    public ResponseEntity<OutputStream> getFile(String url) {

    }
}
