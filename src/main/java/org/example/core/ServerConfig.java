package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.example.core.PackageScanner.getAllClasses;

@Getter
@AllArgsConstructor
public class ServerConfig {
    private int port;
    private String packageName;

    Map<String,Class<?>> map;

    public void populateControllers() throws IOException {
        List<Class<?>> controllers = getAllClasses(this.packageName);

        for (Class<?> controller : controllers) {
            Controller annotation = controller.getAnnotation(Controller.class);
            String key = annotation.path();
            map.put(key, controller);
        }
    }


}
