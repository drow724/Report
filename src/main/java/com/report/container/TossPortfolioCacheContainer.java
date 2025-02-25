package com.report.container;

import com.report.dto.TossPortfolioDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class TossPortfolioCacheContainer {

    private TossPortfolioDTO dto = new TossPortfolioDTO();

    synchronized public void setDto(TossPortfolioDTO dto) {
        this.dto = dto;
    }
}
