package com.example.rqchallenge.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class HttpClientConfig {

    @Bean("closeableHttpClient")
    @Scope("singleton")
    public CloseableHttpClient createClient(){
        return HttpClients.createDefault();
    }
}
