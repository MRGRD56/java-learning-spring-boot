package com.mrgrd56.javalearningspringboot.util;


import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CommandTelnetClient implements Closeable {

    public final TelnetClient telnetClient = new TelnetClient();
    private final InputStream inputStream;
    private final BufferedReader inputReader;
    private final Scanner inputScanner;
    private final PrintWriter outputWriter;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public CommandTelnetClient(String hostname, int port) throws IOException {
        telnetClient.connect(hostname, port);
        inputStream = telnetClient.getInputStream();
        inputReader =  new BufferedReader(new InputStreamReader(inputStream));
        inputScanner = new Scanner(inputStream, telnetClient.getCharset().name());
        outputWriter = new PrintWriter(telnetClient.getOutputStream(), true);
    }

    /**
     * @param callback Accepts the read output and returns isCompleted flag
     */
    public void listenOutput(Function<String, Boolean> callback) {
        asyncExecutor.submit(() -> {
            Boolean isCompleted = false;
            while (!Boolean.TRUE.equals(isCompleted) && inputScanner.hasNext()) {
                String output = inputScanner.next();
                isCompleted = callback.apply(output);
            }
        });
    }

    public String waitOutput() {
        return getFutureResult(waitOutputAsync());
    }

    public String waitOutput(@Nullable Pattern pattern) {
        return getFutureResult(waitOutputAsync(pattern));
    }

    public CompletableFuture<String> waitOutputAsync() {
        return waitOutputAsync(null);
    }

    public CompletableFuture<String> waitOutputAsync(@Nullable Pattern pattern) {
        CompletableFuture<String> outputFuture = new CompletableFuture<>();

        listenOutput(output -> {
            if (pattern == null || pattern.matcher(output).find()) {
                outputFuture.complete(output);
                return true;
            }

            return outputFuture.isCancelled();
        });

        return outputFuture;
    }

    public void sendInput(String input) {
        try {
            telnetClient.getOutputStream().write((input + System.lineSeparator()).getBytes(telnetClient.getCharset()));
            telnetClient.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        outputWriter.println(input);
    }

    public String sendCommand(String command) {
        return getFutureResult(sendCommandAsync(command));
    }

    public CompletableFuture<String> sendCommandAsync(String command) {
//        telnetClient.registerInputListener(() -> {
//            try {
//                String input = new String(telnetClient.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//                System.out.println(input);
//
//                outputResponse.complete(input);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

//        try {
//            inputStream.skip(Integer.MAX_VALUE);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        CompletableFuture<String> outputResponse = waitOutputAsync();

        sendInput(command);

        return outputResponse;
    }

    private <T> T getFutureResult(CompletableFuture<T> future) {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (telnetClient.isConnected()) {
            telnetClient.disconnect();
        }
    }
}
