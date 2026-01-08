package com.example.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class CachedResponse {
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final String body;
    private final long createdAt;

    public CachedResponse(HttpStatus status, HttpHeaders headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.createdAt = System.currentTimeMillis();
    }

    public HttpStatus getStatus() { return status; }
    public HttpHeaders getHeaders() { return headers; }
    public String getBody() { return body; }
    public long getCreatedAt() { return createdAt; }
}
