package com.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReportService {

    public Mono<Object> retrieveReport() {
       return Mono.just("");
    }
}
