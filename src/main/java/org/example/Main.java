package org.example;

import org.example.core.TcpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TcpServer tcpServer = new TcpServer(8086);
        tcpServer.start();
    }
}