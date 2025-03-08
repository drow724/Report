package com.report.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class RestClientConfiguration {

    /*
        OpenAiApi class 사용
    */
    @Bean
    public RestClient.Builder restClientBuilder() {
        ReactorClientHttpRequestFactory factory = new ReactorClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMinutes(10L));
        factory.setReadTimeout(Duration.ofMinutes(10L));
        return RestClient.builder()
                .requestFactory(factory);
    }
}
