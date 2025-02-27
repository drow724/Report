package com.report.service;

import com.report.dto.KBPortfolioDTO;
import com.report.dto.TossPortfolioDTO;
import com.report.response.DailyReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TossPortfolioService tossPortfolioService;

    private final KBPortfolioService kbPortfolioService;

    private final OpenAiService openAiService;

    public Mono<DailyReportResponse> retrieveReport() {
        final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);

        TossPortfolioDTO tossPortfolioDTO = tossPortfolioService.retrieveTossPortfolio();
        KBPortfolioDTO kbPortfolioDTO = kbPortfolioService.retrieveKBPortfolio();

        BigInteger tossTotalInvestment = new BigInteger(sanitize(tossPortfolioDTO.getTotalInvestment()));
        BigInteger kbTotalInvestment = new BigInteger(sanitize(kbPortfolioDTO.getTotalInvestment()));

        String totalInvestment = formatter.format(tossTotalInvestment.add(kbTotalInvestment)) + "원";

        BigInteger tossOriginalInvestment = new BigInteger(sanitize(tossPortfolioDTO.getOriginalInvestment()));
        BigInteger kbOriginalInvestment = new BigInteger(sanitize(kbPortfolioDTO.getOriginalInvestment()));

        String originalInvestment = formatter.format(tossOriginalInvestment.add(kbOriginalInvestment)) + "원";

        String tossPortfolioTotalRevenue = tossPortfolioDTO.getTotalRevenue();
        String[] tossPortfolioTotalRevenueArr = tossPortfolioTotalRevenue.split(" ");

        String tossPortfolioTotalRevenueAmount = tossPortfolioTotalRevenueArr[0];
        BigInteger tossTotalRevenueAmount = new BigInteger(sanitize(tossPortfolioTotalRevenueAmount));

        String kbPortfolioTotalRevenue = kbPortfolioDTO.getTotalRevenue();
        String[] kbPortfolioTotalRevenueArr = kbPortfolioTotalRevenue.split(" ");

        String kbPortfolioTotalRevenueAmount = kbPortfolioTotalRevenueArr[0];
        BigInteger kbTotalRevenueAmount = new BigInteger(sanitize(kbPortfolioTotalRevenueAmount));

        BigInteger zeroInteger = new BigInteger("0");

        BigInteger totalRevenueAmountInteger = tossTotalRevenueAmount.add(kbTotalRevenueAmount);
        String totalRevenueAmount = (totalRevenueAmountInteger.compareTo(zeroInteger) > 0 ? "+" : "-")
                + formatter.format(totalRevenueAmountInteger) + "원";

        String tossPortfolioTotalRevenuePercent = tossPortfolioTotalRevenueArr[1].replaceAll("[^0-9+-.]", "");

        boolean hasAbs = tossPortfolioTotalRevenuePercent.contains("+") || tossPortfolioTotalRevenuePercent.contains("-");

        BigDecimal tossTotalRevenuePercent = new BigDecimal(
                hasAbs ? tossPortfolioTotalRevenuePercent :
                        (tossTotalRevenueAmount.compareTo(zeroInteger) > 0 ? "+" : "-") + tossPortfolioTotalRevenuePercent
        );

        String kbPortfolioTotalRevenuePercent = kbPortfolioTotalRevenueArr[1].replaceAll("[^0-9+-.]", "");
        hasAbs = kbPortfolioTotalRevenuePercent.contains("+") || kbPortfolioTotalRevenuePercent.contains("-");
        BigDecimal kbTotalRevenuePercent = new BigDecimal(
                hasAbs ? kbPortfolioTotalRevenuePercent :
                        (kbTotalRevenueAmount.compareTo(zeroInteger) > 0 ? "+" : "-") + kbPortfolioTotalRevenuePercent
        );

        String totalRevenuePercent = tossTotalRevenuePercent.add(kbTotalRevenuePercent).setScale(2, RoundingMode.FLOOR) + "%";

        DailyReportResponse response = DailyReportResponse
                .builder()
                .totalInvestment(totalInvestment)
                .originalInvestment(originalInvestment)
                .totalRevenue(totalRevenueAmount + " (" + totalRevenuePercent + ")")
                .openAiMarkUpMessage(openAiService.retrieveOpenAiMarkUpMessage())
                .tossPortfolioDTO(tossPortfolioDTO)
                .kbPortfolioDTO(kbPortfolioDTO)
                .build();

       return Mono.just(response);
    }

    private String sanitize(String str) {
        return str.replaceAll("[^0-9()+-]", "");
    }
}
