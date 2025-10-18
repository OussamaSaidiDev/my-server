package org.example.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PackageScanner  {

    public static List<Class<?>> getAllClassesAnnotatedWhit(String packageName,Class<Annotation> annotation) throws IOException {
        Path path = Paths.get("target/classes/" + packageName.replace(".", "/"));
        List<Class<?>> list = new ArrayList<>();
        try(Stream<Path>  stream= Files.list(path)){
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(Path::getFileName)
                    .map(fileName -> fileName.toString().replace(".class", ""))
                    .map(className -> {
                        try {
                            return Class.forName(packageName + "." + className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(aClass -> aClass.isAnnotationPresent(annotation))
                    .forEach(list::add);
        }
        return list;
    }

    public static List<Class<?>> getAllClasses(String packageName) throws IOException {
        Path path = Paths.get("target/classes/" + packageName.replace(".", "/"));
        List<Class<?>> list = new ArrayList<>();
        try(Stream<Path>  stream= Files.list(path)){
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(Path::getFileName)
                    .map(fileName -> fileName.toString().replace(".class", ""))
                    .map(className -> {
                        try {
                            return Class.forName(packageName + "." + className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(list::add);
        }
        return list;
    }
}
