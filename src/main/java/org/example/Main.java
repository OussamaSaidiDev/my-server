package org.example;

import org.apache.velocity.app.Velocity;
import org.example.core.ServerConfig;
import org.example.core.TcpServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

import static org.example.core.PackageScanner.getAllClasses;

public class Main {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Properties props = new Properties();
        props.setProperty("resource.loader", "classpath");
        props.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);
        ServerConfig serverConfig = new ServerConfig(8086,"org.example.controllers",new HashMap<>());
        TcpServer tcpServer = new TcpServer(serverConfig);
        tcpServer.start();

    }
}