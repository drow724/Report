package com.report.service;

import com.microsoft.playwright.*;
import com.report.container.KBPortfolioCacheContainer;
import com.report.dto.KBPortfolioDTO;
import com.report.dto.KBPortfolioDTO.FundDetail;

import com.report.utils.CurrencyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class KBPortfolioService {

    @Value("${kb.password}")
    private String kbPassword;

    @Value("${kb.id}")
    private String kbId;

    @Value("#{environment['spring.profiles.active'] == 'local' ? false : true}")
    private boolean isNotLocal;

    private final KBPortfolioCacheContainer container;
    
    public void generateKBPortfolio() {
        //Playwright 실행
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isNotLocal))) {

            //새탭 활성화
            Page page = browser.newPage();
            //국민은행 내 계좌 페이지로 이동
            page.navigate("https://obank.kbstar.com/quics?page=C055068&QSL=F#loading");
            //보안 프로그램 활성화 시간 기다리기
            page.waitForTimeout(10000L);

            //로그인 시작
            doLogin(page);

            //안랩 프레임 선택
            FrameLocator iframe = page.frameLocator("//*[@id=\"AhnLabInstall-Iframe\"]");

            //하루동안 팝업 다시 열지 않음 체크박스 선택
            Locator securityCheckBtn = iframe.locator("//*[@id=\"checkbox_AhnLabInstall\"]");

            //체크박스 보일 경우에만
            if(securityCheckBtn.isVisible()) {
                //체크박스 선택
                securityCheckBtn.check();
                page.waitForTimeout(1000L);
            }

            //안랩 팝업 닫기 버튼 선택
            Locator securityCloseBtn = iframe.locator("//*[@id=\"b053921\"]/div/div[3]/span/input");

            //팝업 닫기 버튼 보일 경우에만
            if(securityCloseBtn.isVisible()) {
                //팝업 닫기 버튼 클릭
                securityCloseBtn.click();
                page.waitForTimeout(1000L);
            }

            //펀드보기 버튼 선택 및 클릭
            Locator fundBtn = page.locator("//*[@id=\"b049338\"]/ul/li[2]/a");
            fundBtn.click();
            page.waitForTimeout(1000L);

            //펀드 포트폴리오 상세 보기 버튼 선택 및 클릭
            Locator portfolioDetailBtn = page.locator("//*[@id=\"IBF\"]/div[6]/div[1]/div[2]/a[1]");
            portfolioDetailBtn.click();

            //펀드 포트폴리오 조회 기다리기
            page.waitForTimeout(5000L);

            KBPortfolioDTO.KBPortfolioDTOBuilder builder = KBPortfolioDTO.builder();

            //펀드 포트폴리오 이름 선택 및 Text 가져오기
            Locator portfolioNameLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/h3");
            String portfolioTitle = portfolioNameLocator.innerText();
            builder.portfolioTitle(portfolioTitle);

            //펀드 포트폴리오 총 평가금액 선택 및 Text 가져오기
            Locator totalInvestmentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[2]/dl[1]/dd");
            String totalInvestment = totalInvestmentLocator.innerText();
            builder.totalInvestment(totalInvestment);

            //펀드 포트폴리오 총 투자금액 선택 및 Text 가져오기
            Locator originalInvestmentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[2]/dl[2]/dd");
            String originalInvestment = originalInvestmentLocator.innerText();
            builder.originalInvestment(originalInvestment);

            //펀드 포트폴리오 총 투자 수익률
            Locator totalRevenuePercentLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[1]/dl[1]/dd");
            String totalRevenuePercent = totalRevenuePercentLocator.innerText();

            //펀드 포트폴리오 평가손익
            Locator totalRevenueLocator = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[1]/div[1]/div[2]/div[1]/dl[3]/dd");
            String totalRevenue = totalRevenueLocator.innerText();

            //Toss와 데이터 형태 맞춤
            builder.totalRevenue(totalRevenue + " (" + totalRevenuePercent + ")");

            //펀드 포트폴리오 계좌 리스트 선택
            List<Locator> fundDetailLocators = page.locator("//*[@id=\"b059495\"]/div[2]/div/div[2]/div/div[3]/ul/li").all();

            //3자리 마다 ,를 붙이기 위한 formatter ex) 3,506,000원
            final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);

            List<FundDetail> fundDetails = fundDetailLocators.stream().map(fundDetailLocator -> {
                FundDetail.FundDetailBuilder fundDetailBuilder = FundDetail.builder();

                //펀드 이름 및 계좌번호 Wrapper 선택
                Locator goodsLocator = fundDetailLocator.locator("//div[1]");

                //펀드 이름 선택 및 Text 가져오기
                Locator fundNameLocator = goodsLocator.locator("//a/h3/strong");
                String fundName = fundNameLocator.innerText();
                fundDetailBuilder.fundName(fundName);

                //펀드 계좌번호 선택 및 Text 가져오기
                Locator fundBankAccountLocator = goodsLocator.locator("//p[1]/a");
                String fundBankAccount = fundBankAccountLocator.innerText();
                fundDetailBuilder.fundBankAccount(fundBankAccount);

                //펀드 수익률 선택 및 Text 가져오기
                Locator tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[1]/td/div");
                String fundRevenue = tdLocator.innerText();
                fundDetailBuilder.totalReturnRate(fundRevenue);

                //펀드 평가금액 선택 및 Text 가져오기
                tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[2]/td/div");
                String evaluationAmount = tdLocator.innerText();
                fundDetailBuilder.evaluationAmount(evaluationAmount);

                //펀드 투자금액 선택 및 Text 가져오기
                tdLocator = fundDetailLocator.locator("//div[2]/table/tbody/tr[3]/td/div");
                String principalAmount = tdLocator.innerText();
                fundDetailBuilder.principalAmount(principalAmount);

                //총 수익금액 계산
                BigInteger evaluationAmountInteger = new BigInteger(CurrencyUtils.sanitize(evaluationAmount));
                BigInteger principalAmountInteger = new BigInteger(CurrencyUtils.sanitize(principalAmount));

                String totalProfit = formatter.format(evaluationAmountInteger.subtract(principalAmountInteger)) + "원";
                fundDetailBuilder.totalProfit(totalProfit);

                return fundDetailBuilder.build();
            }).toList();

            builder.fundDetails(fundDetails);

            container.setDto(builder.build());
        }
    }

    private void doLogin(Page page) {
        // 아이디/비밀번호로 로그인하기 선택
        Locator aidTab = page.locator("//*[@id=\"aidtab\"]");
        page.waitForTimeout(1000L);

        // 아이디/비밀번호로 로그인하기 클릭
        aidTab.click();
        page.waitForTimeout(1000L);

        //아이디 Input
        Locator id = page.locator("//*[@id=\"txtWWWBnkgLginID\"]");
        id.fill(kbId);
        page.waitForTimeout(1000L);

        //Tab 버튼 눌러서 비밀번호 창으로 focus 이동 및 보안 키패드 활성화
        page.keyboard().press("Tab");
        page.waitForTimeout(1000L);

        //보안키패드 클릭
        for(String token: kbPassword.split(",")) {
            Locator a = page.locator("//*[@id=\"nppfs-keypad-LoginPassword\"]/div/div[2]/img[" + token + "]");
            a.click();
            page.waitForTimeout(1000L);
        }

        //로그인 버튼 클릭
        Locator loginBtn = page.locator("//*[@id=\"idLoginBtn\"]");
        loginBtn.click();

        // 로그인 요청 완료까지 대기
        page.waitForTimeout(10000L);
    }

    public KBPortfolioDTO retrieveKBPortfolio() {
        return container.getDto();
    }
}
