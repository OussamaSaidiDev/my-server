package org.example.core;

import org.example.model.HttpMethod;
import org.example.model.HttpRequest;
import org.example.model.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements AutoCloseable{
    private final ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ServerConfig serverConfig;


    public TcpServer( ServerConfig serverConfig) throws IOException {
        this.serverSocket = new ServerSocket(serverConfig.getPort());
        this.serverConfig = serverConfig;
        System.out.println("Server started on port: " + serverConfig.getPort());
        serverConfig.populateControllers();
        System.out.println("Controllers loaded: " + serverConfig.getMap().size());
    }

    public void start() {
        System.out.println("Server is listening for connections...");
        try (serverSocket;) {
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                executorService.submit(() -> handleClient(clientSocket));
            }
        }catch (IOException e){
            System.out.println("Server error: " + e.getMessage());
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

            System.out.println("Request received: " + inputLine);

            request.setMethod(HttpMethod.valueOf(inputLine.split(" ")[0]));
            request.setPath(inputLine.split(" ")[1]);
            request.setVersion(inputLine.split(" ")[2]);

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.isEmpty()) break;
                request.getHeaders().put(inputLine.split(":")[0],inputLine.split(":")[1]);
            }

            System.out.println("Processing request - Method: " + request.getMethod() + ", Path: " + request.getPath());

            // Create response
            HttpResponse response = handleResponse(request);

            System.out.println("Response status: " + response.getStatus());

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
            System.out.println("Response sent successfully");

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
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
        String body;
        Class<?> controllerClass = serverConfig.getMap().get(request.getPath());

        if (controllerClass != null) {
            System.out.println("Controller found for path: " + request.getPath());
            try {
                Object controller = controllerClass.getDeclaredConstructor().newInstance();
                System.out.println("Controller instance created: " + controllerClass.getSimpleName());

                Method getMethod = controllerClass.getMethod("get");
                Object result = getMethod.invoke(controller);

                body = result.toString();
                response.setStatus(200);
                System.out.println("Controller method executed successfully");

            } catch (Exception e) {
                System.err.println("Error executing controller: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(500);
                body = "<html><body><h1>Internal Server Error</h1><p>" + e.getMessage() + "</p></body></html>";
            }
        } else {
            System.out.println("No controller found for path: " + request.getPath());
            response.setStatus(404);
            body = "<html><body><h1>404 Not Found</h1><p>The requested path was not found</p></body></html>";
        }

        response.setBody(body);
        response.getHeaders().put("Content-Length", String.valueOf(body.getBytes().length));

        return response;
    }



    @Override
    public void close() throws Exception {
        System.out.println("Shutting down server...");
        executorService.shutdown();
        serverSocket.close();
        System.out.println("Server closed");
    }
}