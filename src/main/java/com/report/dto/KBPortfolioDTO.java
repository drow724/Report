package com.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KBPortfolioDTO {

    private String bankAccount;

    private String portfolioTitle;

    private String totalInvestment;

    private String totalRevenue;

    private Boolean isPositive;
}
