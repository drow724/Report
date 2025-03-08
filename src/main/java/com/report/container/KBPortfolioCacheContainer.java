package com.report.container;

import com.report.dto.KBPortfolioDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class KBPortfolioCacheContainer {

    private KBPortfolioDTO dto = new KBPortfolioDTO();

    synchronized public void setDto(KBPortfolioDTO dto) {
        this.dto = dto;
    }
}
