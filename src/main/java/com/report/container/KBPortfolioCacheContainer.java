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


    private String jsessionId = "0000dAolXvxd-J-d7nKlo6qnSQD:BANK10904";

    private String qsid = "19A4&&_9PD36NJrqwlJlHVFVTxZOG";

    synchronized public void setDto(KBPortfolioDTO dto) {
        this.dto = dto;
    }

    synchronized public void setJsessionId(String jsessionId) {
        this.jsessionId = jsessionId;
    }

    synchronized public void setQsid(String qsid) {
        this.qsid = qsid;
    }
}
