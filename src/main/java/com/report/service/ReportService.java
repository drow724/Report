package com.report.service;

import com.report.response.DailyReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TossPortfolioService tossPortfolioService;

    private final KBPortfolioService kbPortfolioService;

    public Mono<DailyReportResponse> retrieveReport() {
        DailyReportResponse response = DailyReportResponse
                .builder()
                .tossPortfolioDTO(tossPortfolioService.retrieveTossPortfolio())
                .kbPortfolioDTO(kbPortfolioService.retrieveKBPortfolio())
                .build();

       return Mono.just(response);
    }
}
