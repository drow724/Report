package com.report.service;

import com.microsoft.playwright.*;
import com.report.container.KBPortfolioCacheContainer;
import com.report.dto.KBPortfolioDTO;
import com.report.dto.KBPortfolioDTO.FundDetail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KBPortfolioService {

    private final KBPortfolioCacheContainer container;

    public void generateKBPortfolio() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions())) {

            Page page = browser.newPage();
            page.navigate("https://obank.kbstar.com/quics?page=C055068&QSL=F#loading");

            page.waitForTimeout(10000L);

            doLogin(page);

            Locator fundBtn = page.locator("//*[@id=\"b049338\"]/ul/li[2]/a");
            fundBtn.click();
            page.waitForTimeout(1000L);

            Locator fundDetailBtn = page.locator("//*[@id=\"btn_Ptflo_1\"]");
            fundDetailBtn.click();
            page.waitForTimeout(1000L);

            KBPortfolioDTO.KBPortfolioDTOBuilder builder = KBPortfolioDTO.builder();

            Locator bankAccountLocator = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/div[1]");
            String bankAccount = bankAccountLocator.innerText();
            builder.bankAccount(bankAccount);

            Locator portfolioNameLocator = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/h3");
            String portfolioTitle = portfolioNameLocator.innerText();
            builder.portfolioTitle(portfolioTitle);

            Locator totalInvestmentLocator = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/ul/li[2]/strong");
            String totalInvestment = totalInvestmentLocator.innerText();
            builder.totalInvestment(totalInvestment);

            Locator isPositiveLocator = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/ul/li[2]/span/b");
            boolean isPositive = "상승".equals(isPositiveLocator.innerText());
            builder.isPositive(isPositive);

            Locator totalInvestmentRevenueLocator = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/ul/li[2]/span");
            String totalRevenue = totalInvestmentRevenueLocator.innerText();
            builder.totalRevenue(totalRevenue);

            List<Locator> fundDetailLocators = page.locator("//*[@id=\"포트폴리오목록_0000339270\"]/div/ul/li").all();

            List<FundDetail> fundDetails = fundDetailLocators.stream().map(fundDetailLocator -> {
                FundDetail.FundDetailBuilder fundDetailBuilder = FundDetail.builder();

                Locator ddLocator = fundDetailLocator.locator("//div[1]/dl/dd");

                Locator fundBankAccountLocator = ddLocator.locator("//span[2]");
                String fundBankAccount = fundBankAccountLocator.innerText();
                fundDetailBuilder.fundBankAccount(fundBankAccount);

                Locator fundNameLocator = ddLocator.locator("//span[3]");
                String fundName = fundNameLocator.innerText();
                fundDetailBuilder.fundName(fundName);

                Locator spanLocator = fundDetailLocator.locator("//div[2]/dl[2]/dd/span");

                String revenueText = spanLocator.innerText();

                String[] revenueArr = revenueText.split("\n");

                boolean isFundPositive = "상승".equals(revenueArr[0]);
                fundDetailBuilder.isFundPositive(isFundPositive);

                String fundRevenue = revenueArr[1];
                fundDetailBuilder.fundRevenue(fundRevenue);

                Locator amountLocator = fundDetailLocator.locator("//div[2]/dl[3]/dd/span");
                String fundAmount = amountLocator.innerText();
                fundDetailBuilder.fundAmount(fundAmount);

                return fundDetailBuilder.build();
            }).toList();

            builder.fundDetails(fundDetails);

            container.setDto(builder.build());
        }
    }

    private void doLogin(Page page) {
        Locator aidTab = page.locator("//*[@id=\"aidtab\"]");
        page.waitForTimeout(1000L);

        aidTab.click();
        page.waitForTimeout(1000L);

        Locator id = page.locator("//*[@id=\"txtWWWBnkgLginID\"]");
        id.fill("DROW724");
        page.waitForTimeout(1000L);

        page.keyboard().press("Tab");
        page.waitForTimeout(1000L);

        Locator a = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[23]");
        a.click();
        page.waitForTimeout(1000L);
        a.click();
        page.waitForTimeout(1000L);

        Locator btn1 = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[3]");
        btn1.click();
        page.waitForTimeout(1000L);
        btn1.click();
        page.waitForTimeout(1000L);

        Locator btn9 = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[11]");
        btn9.click();
        page.waitForTimeout(1000L);

        Locator btn5 = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[7]");
        btn5.click();
        page.waitForTimeout(1000L);

        Locator btn6 = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[8]");
        btn6.click();
        page.waitForTimeout(1000L);

        Locator btn2 = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[4]");
        btn2.click();
        page.waitForTimeout(1000L);

        Locator closeBtn = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[2]");
        closeBtn.click();
        page.waitForTimeout(1000L);

        Locator loginBtn = page.locator("//*[@id=\"idLoginBtn\"]");
        loginBtn.click();

        page.waitForTimeout(10000L);
    }
    public KBPortfolioDTO retrieveKBPortfolio() {
        return container.getDto();
    }
}
