package com.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KBPortfolioDTO {

    private String portfolioTitle;

    private String totalInvestment;

    private String originalInvestment;

    private String totalRevenue;

    private List<FundDetail> fundDetails;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FundDetail {

        private String fundBankAccount;

        private String fundName;

        private String totalReturnRate;

        private String totalProfit;

        private String evaluationAmount;

        private String principalAmount;
    }
}
