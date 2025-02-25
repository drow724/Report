package com.report.repository;

import com.google.common.cache.Cache;
import com.report.constants.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final Cache<CacheType, String> cache;

    public Optional<String> findById(CacheType cacheType) {
        return Optional.ofNullable(cache.getIfPresent(cacheType));
    }

}
