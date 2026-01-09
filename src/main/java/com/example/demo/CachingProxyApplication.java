package com.example.demo;

import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@RestController
public class CachingProxyApplication {

    private final RestTemplate restTemplate;
    private final String originBaseUrl;
    private final CacheService cacheService;

    public CachingProxyApplication(RestTemplate restTemplate,
                                   @Value("${proxy.origin}") String originBaseUrl,
                                   CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.originBaseUrl = originBaseUrl;
        this.cacheService = cacheService;
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello from Spring Boot";
    }

    @GetMapping("/{*path}")
    public ResponseEntity<String> proxyGet(
            @PathVariable("path") String path,
            @RequestParam Map<String, String> queryParams,
            HttpServletRequest request){

        String targetUrl = buildTargetUrl(path, queryParams);
        long start = System.currentTimeMillis();   // start timer
        String cacheKey = "GET " + targetUrl;
        CachedResponse cached = cacheService.get(cacheKey);

        if(cached != null){
            HttpHeaders headers = new HttpHeaders(cached.getHeaders());
            headers.set("X-Cache", "HIT");
            System.out.println(headers.get("X-Cache"));
            long end = System.currentTimeMillis();     // end timer
            long durationMs = end - start;
            System.out.println("Proxy GET " + targetUrl + " took " + durationMs + " ms");
            return ResponseEntity
                    .status(cached.getStatus())
                    .headers(headers)
                    .body(cached.getBody());
        }

        HttpHeaders headers = new HttpHeaders();
        String accept = request.getHeader("Accept");
        if(accept != null){
            headers.add("Accept", accept);
        }

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        CachedResponse toCache = new CachedResponse(
                HttpStatus.valueOf(response.getStatusCode().value()),
                response.getHeaders(),
                response.getBody()
        );
        cacheService.put(cacheKey, toCache);

        HttpHeaders responseHeaders = new HttpHeaders(response.getHeaders());
        responseHeaders.set("X-Cache", "MISS");
        System.out.println(responseHeaders.get("X-Cache"));
        long end = System.currentTimeMillis();     // end timer
        long durationMs = end - start;
        System.out.println("Proxy GET " + targetUrl + " took " + durationMs + " ms");
        return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());
    }

    private String buildTargetUrl(String path, Map<String, String> queryParams){
        StringBuilder url = new StringBuilder(originBaseUrl);

        if(!path.isEmpty()){
            if(!originBaseUrl.endsWith("/") && !path.startsWith("/")){
                url.append("/");
            }
            url.append(path);
        }

        if (!queryParams.isEmpty()) {
            url.append("?");
            url.append(
                    queryParams.entrySet().stream()
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .reduce((a, b) -> a + "&" + b)
                            .orElse("")
            );
        }

        return url.toString();
    }

    @PostMapping("/admin/clear-cache")
    public String clearCache() {
        cacheService.clearAll();
        return "Cache cleared. Entries now: " + cacheService.getSize();
    }
}
