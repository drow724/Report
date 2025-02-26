package com.report.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.report.constants.ReportType;
import com.report.constants.TossPortfolioHeader;
import com.report.container.TossPortfolioCacheContainer;
import com.report.dto.TossPortfolioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TossPortfolioService {

    @Value("${chrome.auth}")
    private String chromeAuth;

    private final TossPortfolioCacheContainer container;

    public void generateTossPortfolio() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions())) {

            Path storagePath = Paths.get(chromeAuth);

            BrowserContext context = Files.exists(storagePath) ? browser.newContext(new Browser.NewContextOptions()
                    .setStorageStatePath(storagePath)) : browser.newContext();

            Page page = context.newPage();

            page.navigate("https://tossinvest.com/investment-portfolio?product=all");

            page.waitForTimeout(5000L);

            Locator idLocator = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[1]/div[1]/div/div/div/input");

            if(idLocator.isVisible()) {
                idLocator.fill("송재근");
                page.waitForTimeout(1000L);

                Locator birthDay = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[1]/div[2]/div/div/div/input");
                birthDay.fill("960626");
                page.waitForTimeout(1000L);

                Locator phoneNumber = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[2]/div/div/div/input");
                phoneNumber.fill("01032279606");
                page.waitForTimeout(1000L);

                Locator checkAll = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[3]/div[1]/div/div/div/input");
                checkAll.click();
                page.waitForTimeout(1000L);

                Locator loginBtn = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/button");
                loginBtn.click();
                page.waitForTimeout(10000L);

                Locator keepLogin = page.locator("//html/body/div[4]/div[2]/div[3]/div[2]/button[2]");
                keepLogin.click();
                page.waitForTimeout(10000L);

                page.navigate("https://tossinvest.com/investment-portfolio?product=all");
                page.waitForTimeout(5000L);
            }

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

            context.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(storagePath));
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
