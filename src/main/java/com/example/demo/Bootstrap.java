package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine;

@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        // 1) Create the Picocli command object (no Spring yet)
        CachingProxyCommand cmdObject = new CachingProxyCommand();

        // 2) Let Picocli parse CLI args
        CommandLine cmd = new CommandLine(cmdObject);
        cmd.parseArgs(args);

        // 3) Use parsed values to set system properties BEFORE Spring Boot starts
        if (cmdObject.getPort() != null) {
            System.setProperty("server.port", cmdObject.getPort().toString());
        }
        if (cmdObject.getOrigin() != null) {
            System.setProperty("proxy.origin", cmdObject.getOrigin());
        }

        // start Spring and get ApplicationContext
        ConfigurableApplicationContext context =
                SpringApplication.run(Bootstrap.class, args);

        if (cmdObject.isClearCache()) {
            CacheService cacheService = context.getBean(CacheService.class);
            cacheService.clearAll();
            System.out.println("Cache cleared from CLI");
            context.close();
            System.exit(0);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
