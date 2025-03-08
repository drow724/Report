package com.report.service;

import com.microsoft.playwright.*;
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

    @Value("#{environment['spring.profiles.active'] == 'local' ? false : true}")
    private boolean isNotLocal;

    private final TossPortfolioCacheContainer container;

    public void generateTossPortfolio() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isNotLocal))) {

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
                if(keepLogin.isVisible()) {
                    keepLogin.click();
                    page.waitForTimeout(10000L);
                }

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

    public TossPortfolioDTO retrieveTossPortfolio() {
        return container.getDto();
    }
}
