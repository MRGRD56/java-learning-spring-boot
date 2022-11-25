package com.mrgrd56.javalearningspringboot.util;


import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetOptionHandler;
import org.springframework.lang.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CommandTelnetClient implements Closeable {

    private final TelnetClient telnetClient = new TelnetClient();
    private final Scanner inputScanner;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public CommandTelnetClient(String hostname, int port) throws IOException {
        telnetClient.connect(hostname, port);
        InputStream inputStream = telnetClient.getInputStream();
        inputScanner = new Scanner(inputStream, telnetClient.getCharset().name());
    }

    public void addOptionHandler(TelnetOptionHandler optionHandler) throws InvalidTelnetOptionException, IOException {
        telnetClient.addOptionHandler(optionHandler);
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

    /**
     * @param callback Accepts the read output and returns isCompleted flag
     */
    public void listenOutputLine(Function<String, Boolean> callback) {
        asyncExecutor.submit(() -> {
            Boolean isCompleted = false;
            while (!Boolean.TRUE.equals(isCompleted) && inputScanner.hasNextLine()) {
                String output = inputScanner.nextLine();
                isCompleted = callback.apply(output);
            }
        });
    }

    public String waitOutput() {
        return getFutureResult(waitOutputAsync());
    }

    public String waitOutput(@Nullable Pattern pattern) {
        return waitOutput(pattern, false);
    }

    public String waitOutput(@Nullable Pattern pattern, boolean isUnmatchedKept) {
        return getFutureResult(waitOutputAsync(pattern, isUnmatchedKept));
    }

    public String waitOutputLine() {
        return getFutureResult(waitOutputLineAsync());
    }

    public String waitOutputLine(@Nullable Pattern pattern) {
        return waitOutputLine(pattern, false);
    }

    public String waitOutputLine(@Nullable Pattern pattern, boolean isUnmatchedKept) {
        return getFutureResult(waitOutputLineAsync(pattern, isUnmatchedKept));
    }

    public CompletableFuture<String> waitOutputAsync() {
        return waitOutputAsync(null, false);
    }

    public CompletableFuture<String> waitOutputLineAsync() {
        return waitOutputLineAsync(null, false);
    }

    private CompletableFuture<String> waitOutputAsyncInternal(@Nullable Pattern pattern, Consumer<Function<String, Boolean>> listener, boolean isUnmatchedKept) {
        CompletableFuture<String> outputFuture = new CompletableFuture<>();

        AtomicReference<String> keptOutput = new AtomicReference<>("");

        listener.accept(output -> {
            keptOutput.updateAndGet(value -> isUnmatchedKept ? value + output : output);
            if (pattern == null || pattern.matcher(keptOutput.get()).find()) {
                outputFuture.complete(keptOutput.get());
                return true;
            }

            return outputFuture.isCancelled();
        });

        return outputFuture;
    }

    public CompletableFuture<String> waitOutputAsync(@Nullable Pattern pattern, boolean isUnmatchedKept) {
        return waitOutputAsyncInternal(pattern, this::listenOutput, isUnmatchedKept);
    }

    public CompletableFuture<String> waitOutputLineAsync(@Nullable Pattern pattern, boolean isUnmatchedKept) {
        return waitOutputAsyncInternal(pattern, this::listenOutputLine, isUnmatchedKept);
    }

    public void sendInput(byte[] bytesInput) {
        try {
            telnetClient.getOutputStream().write(bytesInput);
            telnetClient.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendInput(int[] bytesInput) {
        sendInput(intArrayToBytes(bytesInput));
    }

    public void sendInputLine() {
        sendInput(System.lineSeparator());
    }

    public void sendInputLine(String textInput) {
        sendInput(textInput + System.lineSeparator());
    }

    public void sendInputPackets(String textInput) {
        for (char textChar : textInput.toCharArray()) {
            sendInput(Character.toString(textChar));
        }
    }

    public void sendInput(String textInput) {
        sendInput((textInput).getBytes(telnetClient.getCharset()));
    }

    public String sendCommand(String command) {
        return getFutureResult(sendCommandAsync(command));
    }

    public CompletableFuture<String> sendCommandAsync(String command) {
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

    private byte[] intArrayToBytes(int[] array) {
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (byte) array[i];
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (telnetClient.isConnected()) {
            telnetClient.disconnect();
        }
    }
}
