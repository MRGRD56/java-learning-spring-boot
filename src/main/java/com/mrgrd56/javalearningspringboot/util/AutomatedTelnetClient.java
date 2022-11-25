package com.mrgrd56.javalearningspringboot.util;

import org.apache.commons.net.telnet.*;

import java.io.InputStream;
import java.io.PrintStream;

public class AutomatedTelnetClient {
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    private String prompt = ">>>";

    public AutomatedTelnetClient(String server) {
        try {
            // Connect to the specified server
            telnet.connect(server, 6023);
            TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
            EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
            SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
            try {
                telnet.addOptionHandler(ttopt);
                telnet.addOptionHandler(echoopt);
                telnet.addOptionHandler(gaopt);
            } catch (InvalidTelnetOptionException e) {
                System.err.println("Error registering option handlers: " + e.getMessage());
            }
            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void su(String password) {
//        try {
//            write(“su”);
//            readUntil(“Password: “);
//            write(password);
//            prompt = “#”;
//            readUntil(prompt + ” “);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
            boolean found = false;
            char ch = (char) in.read();
            while (true) {
                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendCommand(String command) {
        try {
            write(command);
            return readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String user = "scrapy";
        String password = "qwe123";
        AutomatedTelnetClient telnet = new AutomatedTelnetClient("localhost");
        // Log the user on
        telnet.readUntil("Username:");
        telnet.write(user);
        telnet.readUntil("Password:");
        telnet.write(password);
        // Advance to a prompt
        telnet.readUntil(telnet.prompt + " ");

        telnet.sendCommand("ps -ef");
        telnet.sendCommand("ls");
        telnet.sendCommand("w");
        telnet.disconnect();
    }
}