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
import java.util.concurrent.*;
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

    private final Semaphore testingSemaphore = new Semaphore(3);

    public void testSemaphore(int number) throws InterruptedException {
        testingSemaphore.acquire();

        Thread.sleep(1000 + (number * 10L));
        System.out.println("number " + number);

        testingSemaphore.release();
    }

    public void startTestingSemaphore() {
        for (var i = 0; i < 500; i++) {
            final int finalI = i;
            new Thread(() -> {
                try {
                    testSemaphore(finalI);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private volatile boolean isPaused = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void testExecutor() {
        for (var i = 0; i < 500; i++) {
            int finalI = i;
            executor.submit(() -> {
                var wasPaused = isPaused;
                if (wasPaused) {
                    System.out.println("i=" + finalI + " paused");
                }
                while (isPaused) {
                    Thread.onSpinWait();
                }

                if (isPaused) {
                    isPaused = false;
                }

                try {
                    Thread.sleep(1000);
                    if (finalI % 5 == 0) {
                        isPaused = true;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("testExecutor = i = " + finalI);
            });
        }
    }
}
