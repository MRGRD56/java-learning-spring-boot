package com.mrgrd56.javalearningspringboot.services;

import com.mrgrd56.javalearningspringboot.util.CommandTelnetClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class ScrapyTelnetService {

    @PostConstruct
    public void work() {
        try (CommandTelnetClient commandTelnetClient = new CommandTelnetClient("localhost", 6023)) {

//            commandTelnetClient.listenOutput((output) -> {
//                System.out.println(output);
////                commandTelnetClient.sendInput("scrapy");
//
//                return false;
//            });

            commandTelnetClient.telnetClient.getInputStream().mark(0);

//            String usernameRequest = commandTelnetClient.waitOutput(Pattern.compile("^Username:$"));
//            commandTelnetClient.sendInput("scrapy");
//            commandTelnetClient.sendInput("e0d56daad1af2e03");

            commandTelnetClient.sendInput("stats.get_stats()\r\n");
//            commandTelnetClient.sendInput("");

//            String output = commandTelnetClient.waitOutput();
            try {
                Thread.sleep(1000);
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//            commandTelnetClient.telnetClient.registerInputListener(() -> {
//
//            });

//            InputStream inputStream = commandTelnetClient.telnetClient.getInputStream();
//            Scanner scanner = new Scanner(inputStream, commandTelnetClient.telnetClient.getCharset());
//            new BufferedReader(new InputStreamReader(inputStream))

//            while (true) {
//                byte[] readBytes = inputStream.readAllBytes();
//                if (readBytes.length > 0) {
//                    String str = new String(readBytes, commandTelnetClient.telnetClient.getCharset());
//                    System.out.println(str);
//                }
//            }

//            String result = scrapyTelnetClient.sendCommand("scrapy\n");
//            String result2 = scrapyTelnetClient.sendCommand("e0d56daad1af2e03\n");
//            String result3 = scrapyTelnetClient.sendCommand("stats.get_stats()\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
