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

    private String bankAccount;

    private String portfolioTitle;

    private String totalInvestment;

    private String totalRevenue;

    private Boolean isPositive;

    private List<FundDetail> fundDetails;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FundDetail {

        private String fundBankAccount;

        private String fundName;

        private Boolean isFundPositive;

        private String fundRevenue;

        private String fundAmount;
    }
}
