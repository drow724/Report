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

    private TossPortfolioDTO tossPortfolioDTO;

    private KBPortfolioDTO kbPortfolioDTO;

}
