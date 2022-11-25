package com.mrgrd56.javalearningspringboot.services;

import com.mrgrd56.javalearningspringboot.util.CommandTelnetClient;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class ScrapyTelnetService {

    @PostConstruct
    public void work() {
        try (CommandTelnetClient commandTelnetClient = new CommandTelnetClient("localhost", 6023)) {

            // Как я понял это позволяет отправлять команду DO ECHO ({0xff, 0xfd, 0x01}) от клиента к серверу
            // По умолчанию почему-то отправляется DONT ECHO ({0xff, 0xfe, 0x01}) после того как был запрошен пароль
            // Из-за этого сервер перестаёт отдавать какой-либо вывод
            // https://www.ibm.com/docs/en/zos/2.3.0?topic=problems-telnet-commands-options
            commandTelnetClient.addOptionHandler(new EchoOptionHandler(true, true, true, true));

            commandTelnetClient.waitOutput(Pattern.compile("Username:\s*$"), true);
            commandTelnetClient.sendInputLine("scrapy");

            commandTelnetClient.waitOutput(Pattern.compile("Password:\s*$"), true);
            commandTelnetClient.sendInputLine("qwe123");

            String authOutput = commandTelnetClient.waitOutput(Pattern.compile("\\S"), true);

            if (authOutput.equalsIgnoreCase("Authentication")) {
                String nextWord = commandTelnetClient.waitOutput(Pattern.compile("\\S"), true);
                throw new RuntimeException("Authentication " + nextWord);
            }

            commandTelnetClient.sendInputLine("stats.get_stats()");

            commandTelnetClient.waitOutputLine(Pattern.compile("stats.get_stats\\(\\)"));
            String result = commandTelnetClient.waitOutputLine(Pattern.compile("\\S"));
            Object a = 2;
        } catch (IOException | InvalidTelnetOptionException e) {
            throw new RuntimeException(e);
        }
    }
}
