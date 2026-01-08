package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@RestController
public class CachingProxyApplication {

    private final RestTemplate restTemplate;
    private final String originBaseUrl;

    public CachingProxyApplication(RestTemplate restTemplate,
                                   @Value("${proxy.origin}") String originBaseUrl) {
        this.restTemplate = restTemplate;
        this.originBaseUrl = originBaseUrl;
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello from Spring Boot";
    }

//    @GetMapping("/products")
//    public ResponseEntity<String> proxyProducts(){
//        String url = originBaseUrl + "/products";
//
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//    }

    @GetMapping("/{*path}")
    public ResponseEntity<String> proxyGet(@PathVariable("path") String path, @RequestParam Map<String, String> queryParams){
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

        ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }
}
