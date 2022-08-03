package com.mrgrd56.javalearningspringboot.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class ConcurrencyService {
    public List<Map<String, Object>> getEntries() throws InterruptedException {
        var threadPool = Executors.newFixedThreadPool(25);

        var allEntries = new ArrayList<Map<String, Object>>();

        var futures = threadPool.invokeAll(
                IntStream.range(0, 25)
                        .mapToObj(index -> (Callable<Void>) () -> {
                            var entries = fetchEntries(index);
                            allEntries.addAll(entries);
                            return null;
                        })
                        .toList());

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        return allEntries;
    }

    private List<Map<String, Object>> fetchEntries(int index) {
        try {
            var response = new RestTemplate().exchange(
                    "https://api.publicapis.org/entries",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Map<String, Object>>() { }
            );

            var body = response.getBody();
            return (List<Map<String, Object>>) body.get("entries");
        } catch (HttpClientErrorException.TooManyRequests exception) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return fetchEntries(index);
        }
    }
}
