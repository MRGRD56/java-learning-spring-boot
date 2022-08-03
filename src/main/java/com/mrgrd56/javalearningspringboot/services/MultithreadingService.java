package com.mrgrd56.javalearningspringboot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MultithreadingService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long[] numbers = new long[100];

    public void startSynchronized() {
        new Thread(() -> {
            log.info("1");
            synchronized (numbers) {
                try {
                    log.info("2");
                    Thread.sleep(1000);
                    log.info("3");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("4");
        }).start();

        new Thread(() -> {
            log.info("6");
            synchronized (numbers) {
                log.info("7");
                numbers[0] = 231;
                log.info("8");
            }
        }).start();

//        for (var i = 0; i < 100; i++) {
//            final var index = i;
//            new Thread(() -> {
//                synchronized (numbers) {
//                    numbers[99 - index] = index;
//                }
//            }).start();
//        }
    }

    private volatile int volatileNumber = 0;
    private final AtomicInteger atomicNumber = new AtomicInteger(0);

    public void startVolatileAndAtomic() throws InterruptedException {
        var threads = new ArrayList<Thread>();

        for (var i = 0; i < 100_000; i++) {
            var thread = new Thread(() -> {
                volatileNumber++;
                atomicNumber.incrementAndGet();
            });
            threads.add(thread);
            thread.start();
        }

        for (var thread : threads) {
            thread.join();
        }

        log.info("Finished with value1 = {}", volatileNumber);
        log.info("Finished with value2 = {}", atomicNumber.get());
    }
}
