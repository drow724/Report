package com.report.response;

import com.report.dto.KBPortfolioDTO;
import com.report.dto.TossPortfolioDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportResponse {

    private String totalInvestment;

    private String originalInvestment;

    private String totalRevenue;

    private String openAiMarkUpMessage;

    private TossPortfolioDTO tossPortfolioDTO;

    private KBPortfolioDTO kbPortfolioDTO;

}
