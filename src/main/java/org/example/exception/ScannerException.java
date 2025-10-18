package org.example.exception;

public class ScannerException extends RuntimeException{
    public static final String NO_CLASS_FOUND = "no class found";

    public  ScannerException(String message){
        super(message);
    }
}
