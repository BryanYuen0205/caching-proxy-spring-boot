package com.example.demo;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
        name = "caching-proxy",
        mixinStandardHelpOptions = true,
        description = "Starts the caching proxy server"
)
public class CachingProxyCommand implements Runnable{
    @Option(names = "--port", description = "Port for the proxy server")
    Integer port;

    @Option(names = "--origin", description = "Origin server base URL")
    String origin;

    @Option(names = "--clear-cache", description = "Clear cache then exit")
    boolean clearCache;

    @Override
    public void run() {}

    public Integer getPort(){
        return port;
    }

    public String getOrigin() {
        return origin;
    }

    public boolean isClearCache(){
        return clearCache;
    }
}
