package com.report.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.report.constants.ReportType;
import com.report.constants.TossPortfolioHeader;
import com.report.container.TossPortfolioCacheContainer;
import com.report.dto.TossPortfolioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TossPortfolioService {

    private final TossPortfolioCacheContainer container;

    public void generateTossPortfolio() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions())) {
            BrowserContext context = browser.newContext();
            context.addCookies(List.of(
                    new Cookie("AMP_7e34840f59", "JTdCJTIyZGV2aWNlSWQlMjIlM0ElMjI2OTI4YTQyNS03ZDk5LTRmMjMtOTM2Ni1mYjg3ZTg5NzY4YjklMjIlMkMlMjJzZXNzaW9uSWQlMjIlM0ExNzQwMzcwOTUyNjAwJTJDJTIyb3B0T3V0JTIyJTNBZmFsc2UlMkMlMjJsYXN0RXZlbnRUaW1lJTIyJTNBMTc0MDM3MDk1MjYzMCUyQyUyMmxhc3RFdmVudElkJTIyJTNBMiU3RA==")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("AMP_MKTG_7e34840f59", "JTdCJTIycmVmZXJyZXIlMjIlM0ElMjJodHRwcyUzQSUyRiUyRnd3dy5nb29nbGUuY29tJTJGJTIyJTJDJTIycmVmZXJyaW5nX2RvbWFpbiUyMiUzQSUyMnd3dy5nb29nbGUuY29tJTIyJTdE")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("BTK", "+MNV/ygJhGeoZTek9qQQZUVlyUD6yVe8yx/uOPbSouw=")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("FTK", "zzmIFBfiqT3xFX8TIOgWg41akW+lCri1KQEE4Jc9vSHoJ7AcCW/1/+jkHqy7ckDqxfUH4mbRUQTBZ+zrcIbxYzFTI+98r65MaB7UVDF310pog/0ekZHe1A8XxCwGZtjuM1Y8cKXNF751VJzQ/Ob7wUTxkmrkS99Agq3BT8WuPZiN0fsFKETkQszTZUKS+4gLTb0GStTfb/DcuwTH9IrQvfwZWf6UPTMmfGU+W4qFJ4lWCir26gzxrrFfHTMyZHvwi8KKErVIZubnNqXLIxR07g==")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("SESSION", "OTRhZWE4NGMtOTBjMC00ZDhlLTg3NGQtYzYyMzk3NmQzYTg2")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("UTK", "8DXnVukGa0OMzzOfYXDVikxp2loUGsu1V89XutzuMlT70YiAkuJ8F114AcID7JjdVdaksNb84gam6oJIt07+lNPQh5RGj+FhEkxac5UhF+gx2qSrwFaUeOZ8u7S+24vMcHtjGm//mt8J7psJhnfVDn/EzCAde5XVOFI9i1TjY8vnPgdSIswS6kpVzfapldq0")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("XSRF-TOKEN", "75ff8fe9-80d8-451e-a894-79f74a6fb6a0")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_browserId", "9b8e9cd54b574bc08db7e0344c38e8f9")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_fbp", "fb.1.1740370952637.799260261224315863")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_ga", "GA1.1.918321064.1727596309")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_ga_47NPRZ3ZEE", "GS1.1.1740370930.1.0.1740370944.0.0.0")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_ga_9ZSX4ZMJHK", "GS1.1.1730289386.2.1.1730289397.0.0.0")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_ga_TBW87HKGRS", "GS1.1.1740370953.1.0.1740370957.56.0.0")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_hjSessionUser_1949956", "eyJpZCI6ImVhMGQ2OGU1LWVmM2MtNTE1MC1hMmQ5LTUxMmRhNGI2NTEwMyIsImNyZWF0ZWQiOjE3MjA4NzI5MTg4ODIsImV4aXN0aW5nIjp0cnVlfQ==")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("_hjSessionUser_2505863", "eyJpZCI6IjYzYWVlODU2LWFlMWMtNThhYi04NGJjLTdlNTUyODgwZDFkMiIsImNyZWF0ZWQiOjE3NDAzNzA5NTI2NjIsImV4aXN0aW5nIjpmYWxzZX0=")
                            .setDomain(".toss.im")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("browserSessionId", "58721408d246420296514ba26eeae988")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("deviceId", "WTS-0f70f36728b647ddb63f0f4b10c53771")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true),
                    new Cookie("x-toss-distribution-id", "12345")
                            .setDomain(".tossinvest.com")
                            .setPath("/")
                            .setHttpOnly(true)
                            .setSecure(true)
            ));

            Page page = context.newPage();

            page.navigate("https://tossinvest.com/investment-portfolio?product=all");

            page.waitForTimeout(5000L);

            String mainSection = "//*[@id=\"__next\"]/div/div[1]/main/main/section/section";
            Locator totalInvestmentLocator = page.locator(mainSection + "/div[1]/div[1]/div/span");

            String totalInvestment = totalInvestmentLocator.innerText();

            Locator originalInvestmentLocator = page.locator(mainSection + "/div[1]/div[2]/div[1]/div/span[2]");

            String originalInvestment = originalInvestmentLocator.innerText();

            Locator totalRevenueLocator = page.locator(mainSection + "/div[1]/div[2]/div[2]/div/span[2]");

            String totalRevenue = totalRevenueLocator.innerText();

            Locator dailyRevenueLocator = page.locator(mainSection + "/div[1]/div[2]/div[3]/div/span[2]");

            String dailyRevenue = dailyRevenueLocator.innerText();

            Locator domesticLocator = page.locator("//*[@id=\"all-tab\"]/section[1]/header/div/div/span[1]");

            String domestic = domesticLocator.innerText();
            String domesticTotal = domestic.split(" · ")[1];

            Locator domesticRevenueLocator = page.locator("//*[@id=\"all-tab\"]/section[1]/header/div/div/span[2]/span");

            String domesticRevenue = domesticRevenueLocator.innerText();

            List<Locator> domesticLocators = page.locator("//*[@id=\"all-tab\"]/section[1]/div/div/div/div/table/tbody/tr").all();

            Locator domesticHeaderTr = page.locator("//*[@id=\"all-tab\"]/section[1]/div/div/div/div/table/thead/tr");

            List<Locator> domesticHeaderThs = domesticHeaderTr.locator("th").all();

            List<Map<String, Object>> domesticsList = new ArrayList<>();

            for(int i = 1; i < domesticLocators.size(); i++) {
                Locator contentTr = domesticLocators.get(i);
                List<Locator> contentTds = contentTr.locator("td").all();

                Map<String, Object> domesticTable = IntStream.range(0, contentTds.size())
                        .mapToObj(j -> {
                            Locator headerTh = domesticHeaderThs.get(j);
                            String key = headerTh.innerText().trim();

                            TossPortfolioHeader header = TossPortfolioHeader.fromName(key);
                            String headerName = header.getVariableName();

                            Locator contentTd = contentTds.get(j);
                            String value = contentTd.innerText().trim();

                            return new AbstractMap.SimpleEntry<>(headerName, value);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                domesticsList.add(domesticTable);
            }

            Locator overSeasLocator = page.locator("//*[@id=\"all-tab\"]/section[2]/header/div/div/span[1]");

            String overSeas = overSeasLocator.innerText();
            String overSeasTotal = overSeas.split(" · ")[1];

            Locator overSeasRevenueLocator = page.locator("//*[@id=\"all-tab\"]/section[2]/header/div/div/span[2]/span");

            String overSeasRevenue = overSeasRevenueLocator.innerText();

            List<Locator> overSeasLocators = page.locator("//*[@id=\"all-tab\"]/section[2]/div/div/div/div/table/tbody/tr").all();

            Locator overSeasHeaderTr = page.locator("//*[@id=\"all-tab\"]/section[2]/div/div/div/div/table/thead/tr");

            List<Locator> overSeasHeaderThs = overSeasHeaderTr.locator("th").all();

            List<Map<String, Object>> overSeasList = new ArrayList<>();

            for(int i = 1; i < overSeasLocators.size(); i++) {
                Locator contentTr = overSeasLocators.get(i);
                List<Locator> contentTds = contentTr.locator("td").all();

                Map<String, Object> overSeasTable = IntStream.range(0, contentTds.size())
                        .mapToObj(j -> {
                            Locator headerTh = overSeasHeaderThs.get(j);
                            String key = headerTh.innerText().trim();

                            TossPortfolioHeader header = TossPortfolioHeader.fromName(key);
                            String headerName = header.getVariableName();

                            Locator contentTd = contentTds.get(j);
                            String value = contentTd.innerText().trim();

                            return new AbstractMap.SimpleEntry<>(headerName, value);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                overSeasList.add(overSeasTable);
            }

            TossPortfolioDTO dto = TossPortfolioDTO
                    .builder()
                    .totalInvestment(totalInvestment)
                    .originalInvestment(originalInvestment)
                    .totalRevenue(totalRevenue)
                    .dailyRevenue(dailyRevenue)
                    .overSeasTotal(overSeasTotal)
                    .overSeasRevenue(overSeasRevenue)
                    .overSeasList(overSeasList)
                    .domesticTotal(domesticTotal)
                    .domesticRevenue(domesticRevenue)
                    .domesticsList(domesticsList)
                    .build();

            container.setDto(dto);
        }
    }

    /**
     * 원본 텍스트를 줄 단위로 분리한 후, 해외주식/국내주식 섹션을 찾아서
     * Markdown 표로 변환합니다.
     */
    private String convertToMarkdown(String input) {
        // 1. 줄 단위로 분리하고 빈 줄 제거
        String[] lines = input.split("\\r?\\n");
        List<String> nonEmptyLines = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                nonEmptyLines.add(trimmed);
            }
        }

        StringBuilder sb = new StringBuilder();
        // 기본 투자 정보는 첫 6줄로 가정 (원문에 따라 조정 가능)
        if (nonEmptyLines.size() >= 6) {
            sb.append("**").append(nonEmptyLines.get(0)).append("**\n\n");
            sb.append(nonEmptyLines.get(1)).append("\n\n");
            sb.append(nonEmptyLines.get(2)).append("\n\n");
            sb.append(nonEmptyLines.get(3)).append("\n\n");
            sb.append(nonEmptyLines.get(4)).append("\n\n");
            sb.append(nonEmptyLines.get(5)).append("\n\n");
        }

        // 해외주식 섹션 변환
        sb.append(convertSectionToMarkdown(ReportType.OVERSEAS, nonEmptyLines));
        // 국내주식 섹션 변환
        sb.append(convertSectionToMarkdown(ReportType.DOMESTIC, nonEmptyLines));

        return sb.toString();
    }

    /**
     * 지정한 섹션(예:"### 해외주식" 또는 "### 국내주식")을 찾아 Markdown 표로 변환하여 반환합니다.
     * 섹션 헤더 바로 다음 10줄이 헤더 항목이고, 이후 10줄씩이 데이터 행이라고 가정합니다.
     */
    private String convertSectionToMarkdown(ReportType reportType, List<String> lines) {
        StringBuilder sb = new StringBuilder();
        int startIndex = -1;

        // 섹션 시작 위치 찾기
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(reportType.getName())) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
            return "";
        }

        // 섹션 제목 추가 (이미 Markdown 형식이면 그대로 사용)
        sb.append("### ").append(lines.get(startIndex)).append("\n\n");

        // 헤더 항목: 섹션 제목 다음 10줄 (헤더가 10개라고 가정)
        int headerStart = startIndex + 1;
        if (headerStart + 10 > lines.size()) {
            return sb.toString();
        }
        List<String> headers = lines.subList(headerStart, headerStart + 10);

        // Markdown 테이블 헤더 생성
        sb.append("| ");
        for (String header : headers) {
            sb.append(header).append(" | ");
        }
        sb.append("\n|");
        sb.append(" --- |".repeat(headers.size()));
        sb.append("\n");

        // 데이터 행: 헤더 이후부터 섹션 끝까지, 각 10줄씩 묶기
        int dataStart = headerStart + 10;
        // 섹션 끝은 다음 섹션 시작 전까지 혹은 리스트 끝까지
        int sectionEnd = lines.size();
        for (int i = dataStart; i < sectionEnd; ) {
            // 만약 다음 섹션(또는 다른 "###" 시작)이 나오면 break
            if (lines.get(i).startsWith(reportType.getOpposite())) {
                break;
            }
            // 데이터 행은 10줄씩
            if (i + 10 <= sectionEnd) {
                List<String> row = lines.subList(i, i + 10);
                sb.append("| ");
                for (String cell : row) {
                    sb.append(cell).append(" | ");
                }
                sb.append("\n");
                i += 10;
            } else {
                break;
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public TossPortfolioDTO retrieveTossPortfolio() {
        return container.getDto();
    }
}
