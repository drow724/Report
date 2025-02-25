package com.report.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TossPortfolioHeader {
    STOCK_NAME("종목명", "stockName"),
    TOTAL_RETURN_RATE("총 수익률", "totalReturnRate"),
    TOTAL_PROFIT("총 수익금", "totalProfit"),
    AVERAGE_PRICE_PER_SHARE("1주 평균금액", "averagePricePerShare"),
    CURRENT_PRICE("현재가", "currentPrice"),
    QUANTITY_OWNED("보유 수량", "quantityOwned"),
    EVALUATION_AMOUNT("평가금", "evaluationAmount"),
    PRINCIPAL_AMOUNT("원금", "principalAmount"),
    DAILY_RETURN_RATE("일간 수익률", "dailyReturnRate"),
    DAILY_PROFIT("일간 수익금", "dailyProfit"),
    ;

    private final String name;

    private final String variableName;

    // 한글 이름을 영어로 변환하는 메소드
    public static TossPortfolioHeader fromName(String name) {
        for (TossPortfolioHeader attribute : TossPortfolioHeader.values()) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("No matching enum for: " + name);
    }
}
