package com.report.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class WebClientConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        ReactorClientHttpRequestFactory factory = new ReactorClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(120));
        factory.setReadTimeout(Duration.ofSeconds(120));
        return RestClient.builder()
                .requestFactory(factory);
    }
}
