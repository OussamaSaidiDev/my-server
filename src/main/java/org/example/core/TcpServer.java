package org.example.core;

import org.example.model.HttpMethod;
import org.example.model.HttpRequest;
import org.example.model.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements AutoCloseable{
    private final ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TcpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {

        System.out.println("Server started on port " + serverSocket.getLocalPort());
        try (serverSocket;
        ) {
            while (true){
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            HttpRequest request = new HttpRequest();
            inputLine = in.readLine();
            request.setMethod(HttpMethod.valueOf(inputLine.split(" ")[0]));
            request.setPath(inputLine.split(" ")[1]);
            request.setVersion(inputLine.split(" ")[2]);

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.isEmpty()) break;
                request.getHeaders().put(inputLine.split(":")[0],inputLine.split(":")[1]);
            }

            // Create response
            HttpResponse response = handleResponse(request);

            // Write status line
            out.print(response.getVersion() + " " + response.getStatus() + " OK\r\n");

            // Write headers
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                out.print(header.getKey() + ": " + header.getValue() + "\r\n");
            }

            // Blank line separating headers from body
            out.print("\r\n");

            // Write body
            if (response.getBody() != null) {
                out.print(response.getBody());
            }

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse handleResponse(HttpRequest request){
        HttpResponse response = new HttpResponse();
        response.setVersion(request.getVersion());
        response.setStatus(200);

        // Add headers
        response.getHeaders().put("Content-Type", "text/html; charset=UTF-8");
        response.getHeaders().put("Server", "CustomJavaServer/1.0");
        response.getHeaders().put("Connection", "close");
        response.getHeaders().put("Date", new java.util.Date().toString());

        // Set response body
        String body = "<html><body><h1>Hello World</h1><p>Request received successfully</p></body></html>";
        response.setBody(body);
        response.getHeaders().put("Content-Length", String.valueOf(body.getBytes().length));

        return response;
    }



    @Override
    public void close() throws Exception {
        executorService.shutdown();
        serverSocket.close();
    }
}