package com.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossPortfolioDTO {

    private String totalInvestment;

    private String originalInvestment;

    private String totalRevenue;

    private String dailyRevenue;

    private String overSeasTotal;

    private String overSeasRevenue;

    private List<Map<String, Object>> overSeasList;

    private String domesticTotal;

    private String domesticRevenue;

    private List<Map<String, Object>> domesticsList;
}
