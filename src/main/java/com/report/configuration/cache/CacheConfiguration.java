package com.report.configuration.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.report.constants.CacheType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<CacheType, String> tossPortfolioCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(1)
                .build();
    }
}
