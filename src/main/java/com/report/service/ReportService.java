package com.report.service;

import com.report.dto.KBPortfolioDTO;
import com.report.dto.TossPortfolioDTO;
import com.report.response.DailyReportResponse;
import com.report.utils.CurrencyUtils;
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
        //3자리 마다 ,를 붙이기 위한 formatter ex) 3,506,000원
        final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);

        //컨테이너 데이터 불러오기
        TossPortfolioDTO tossPortfolioDTO = tossPortfolioService.retrieveTossPortfolio();
        KBPortfolioDTO kbPortfolioDTO = kbPortfolioService.retrieveKBPortfolio();

        //총 평가 금액에 숫자와 +,- 빼고 전부 제거
        BigInteger tossTotalInvestment = new BigInteger(CurrencyUtils.sanitize(tossPortfolioDTO.getTotalInvestment()));
        BigInteger kbTotalInvestment = new BigInteger(CurrencyUtils.sanitize(kbPortfolioDTO.getTotalInvestment()));

        //총 평가 금액 더하기
        String totalInvestment = formatter.format(tossTotalInvestment.add(kbTotalInvestment)) + "원";

        //총 투자 금액에 숫자와 +,- 빼고 전부 제거
        BigInteger tossOriginalInvestment = new BigInteger(CurrencyUtils.sanitize(tossPortfolioDTO.getOriginalInvestment()));
        BigInteger kbOriginalInvestment = new BigInteger(CurrencyUtils.sanitize(kbPortfolioDTO.getOriginalInvestment()));

        //총 투자 금액 더히가
        String originalInvestment = formatter.format(tossOriginalInvestment.add(kbOriginalInvestment)) + "원";

        //ex) -32,983원 (1.6%)
        String tossPortfolioTotalRevenue = tossPortfolioDTO.getTotalRevenue();

        //[0] : -32,983원, [1] : (1.6%)
        String[] tossPortfolioTotalRevenueArr = tossPortfolioTotalRevenue.split(" ");

        //숫자와 +,- 빼고 전부 제거
        String tossPortfolioTotalRevenueAmount = tossPortfolioTotalRevenueArr[0];
        BigInteger tossTotalRevenueAmount = new BigInteger(CurrencyUtils.sanitize(tossPortfolioTotalRevenueAmount));

        //ex) -32,983원 (1.6%)
        String kbPortfolioTotalRevenue = kbPortfolioDTO.getTotalRevenue();

        //[0] : -32,983원, [1] : (1.6%)
        String[] kbPortfolioTotalRevenueArr = kbPortfolioTotalRevenue.split(" ");

        //숫자와 +,- 빼고 전부 제거
        String kbPortfolioTotalRevenueAmount = kbPortfolioTotalRevenueArr[0];
        BigInteger kbTotalRevenueAmount = new BigInteger(CurrencyUtils.sanitize(kbPortfolioTotalRevenueAmount));

        BigInteger zeroInteger = new BigInteger("0");

        //총 수익 금액 더하기
        BigInteger totalRevenueAmountInteger = tossTotalRevenueAmount.add(kbTotalRevenueAmount);

        //총 수익 금액이 0 초과일 경우 +, 아닐 경우 - 붙이기
        String totalRevenueAmount = (totalRevenueAmountInteger.compareTo(zeroInteger) > 0 ? "+" : "-")
                + formatter.format(totalRevenueAmountInteger) + "원";

        //ex) [1] : (1.6%) -> 숫자와 .,+,-빼고 제거
        String tossPortfolioTotalRevenuePercent = tossPortfolioTotalRevenueArr[1].replaceAll("[^0-9+-.]", "");

        //+,- 기호가 있는지 확인
        boolean hasAbs = tossPortfolioTotalRevenuePercent.contains("+") || tossPortfolioTotalRevenuePercent.contains("-");

        // hasAbs 변수 확인해서 음수, 양수 지정, false면 총 수익금액이 양수인지 음수인지 확인
        BigDecimal tossTotalRevenuePercent = new BigDecimal(
                hasAbs ? tossPortfolioTotalRevenuePercent :
                        (tossTotalRevenueAmount.compareTo(zeroInteger) > 0 ? "+" : "-") + tossPortfolioTotalRevenuePercent
        );

        //ex) [1] : (1.6%) -> 숫자와 .,+,-빼고 제거
        String kbPortfolioTotalRevenuePercent = kbPortfolioTotalRevenueArr[1].replaceAll("[^0-9+-.]", "");

        //+,- 기호가 있는지 확인
        hasAbs = kbPortfolioTotalRevenuePercent.contains("+") || kbPortfolioTotalRevenuePercent.contains("-");

        // hasAbs 변수 확인해서 음수, 양수 지정, false면 총 수익금액이 양수인지 음수인지 확인
        BigDecimal kbTotalRevenuePercent = new BigDecimal(
                hasAbs ? kbPortfolioTotalRevenuePercent :
                        (kbTotalRevenueAmount.compareTo(zeroInteger) > 0 ? "+" : "-") + kbPortfolioTotalRevenuePercent
        );

        //두자리 수 초과는 내림
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
}
