package com.report.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.report.dto.KBPortfolioDTO;
import com.report.dto.TossPortfolioDTO;
import com.report.service.KBPortfolioService;
import com.report.service.OpenAiService;
import com.report.service.TossPortfolioService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CacheScheduler {

    private final TossPortfolioService tossPortfolioService;

    private final KBPortfolioService kBPortfolioService;

    private final OpenAiService openAiService;

    private final ObjectMapper mapper;

    @PostConstruct
    public void postConstruct() throws JsonProcessingException {
        generateCache();
    }

    // 매일 오전 9시 정각에 캐시 초기화
    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void generateCache() throws JsonProcessingException {
        tossPortfolioService.generateTossPortfolio();

        Map<String, Object> portfolioMap = new HashMap<>();
        TossPortfolioDTO tossPortfolioDTO = tossPortfolioService.retrieveTossPortfolio();

        portfolioMap.put("tossPortfolioDTO", tossPortfolioDTO);

        kBPortfolioService.generateKBPortfolio();

        KBPortfolioDTO kbPortfolioDTO = kBPortfolioService.retrieveKBPortfolio();
        portfolioMap.put("kbPortfolioDTO", kbPortfolioDTO);

        String portfolio = mapper.writeValueAsString(portfolioMap);

        openAiService.generateOpenAi(portfolio);
    }
}
