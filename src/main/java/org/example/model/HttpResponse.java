package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class HttpResponse {
    private String version;
    private int status;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}