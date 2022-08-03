package com.mrgrd56.javalearningspringboot;

import com.mrgrd56.javalearningspringboot.services.AppService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaLearningSpringBootApplication implements CommandLineRunner {
    private final AppService appService;

    public JavaLearningSpringBootApplication(AppService appService) {
        this.appService = appService;
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaLearningSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        appService.start();
    }
}
