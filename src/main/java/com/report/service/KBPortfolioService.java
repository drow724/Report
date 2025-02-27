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
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false))) {

            Page page = browser.newPage();
            page.navigate("https://obank.kbstar.com/quics?page=C055068&QSL=F#loading");

            page.waitForTimeout(10000L);

            doLogin(page);

            FrameLocator iframe = page.frameLocator("//*[@id=\"AhnLabInstall-Iframe\"]");

            Locator securityCheckBtn = iframe.locator("//*[@id=\"checkbox_AhnLabInstall\"]");

            if(securityCheckBtn.isVisible()) {
                securityCheckBtn.check();
                page.waitForTimeout(1000L);
            }

            Locator securityCloseBtn = iframe.locator("//*[@id=\"b053921\"]/div/div[3]/span/input");

            if(securityCloseBtn.isVisible()) {
                securityCloseBtn.click();
                page.waitForTimeout(1000L);
            }

            Locator fundBtn = page.locator("//*[@id=\"b049338\"]/ul/li[2]/a");
            fundBtn.click();
            page.waitForTimeout(1000L);

            Locator portfolioDetailBtn = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/div[2]/a[1]");
            portfolioDetailBtn.click();
            page.waitForTimeout(5000L);

            KBPortfolioDTO.KBPortfolioDTOBuilder builder = KBPortfolioDTO.builder();

            Locator portfolioNameLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/h3");
            String portfolioTitle = portfolioNameLocator.innerText();
            builder.portfolioTitle(portfolioTitle);

            Locator totalInvestmentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[2]/dl[1]/dd");
            String totalInvestment = totalInvestmentLocator.innerText();
            builder.totalInvestment(totalInvestment);

            Locator originalInvestmentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[2]/dl[2]/dd");
            String originalInvestment = originalInvestmentLocator.innerText();
            builder.originalInvestment(originalInvestment);

            Locator totalRevenuePercentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[1]/dl[1]/dd");
            String totalRevenuePercent = totalRevenuePercentLocator.innerText();

            Locator totalRevenueLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[1]/dl[3]/dd");
            String totalRevenue = totalRevenueLocator.innerText();

            builder.totalRevenue(totalRevenue + " (" + totalRevenuePercent + ")");

            List<Locator> fundDetailLocators = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[3]/ul/li").all();

            List<FundDetail> fundDetails = fundDetailLocators.stream().map(fundDetailLocator -> {
                FundDetail.FundDetailBuilder fundDetailBuilder = FundDetail.builder();

                Locator goodsLocator = fundDetailLocator.locator("//div[1]");

                Locator fundNameLocator = goodsLocator.locator("//a/h3/strong");
                String fundName = fundNameLocator.innerText();
                fundDetailBuilder.fundName(fundName);

                Locator fundBankAccountLocator = goodsLocator.locator("//p[1]/a");
                String fundBankAccount = fundBankAccountLocator.innerText();
                fundDetailBuilder.fundBankAccount(fundBankAccount);

                Locator tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[1]/td/div");
                String fundRevenue = tdLocator.innerText();
                fundDetailBuilder.totalReturnRate(fundRevenue);

                tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[2]/td/div");
                String evaluationAmount = tdLocator.innerText();
                fundDetailBuilder.evaluationAmount(evaluationAmount);

                tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[3]/td/div");
                String principalAmount = tdLocator.innerText();
                fundDetailBuilder.principalAmount(principalAmount);

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
