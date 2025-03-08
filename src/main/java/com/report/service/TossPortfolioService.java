package com.report.service;

import com.microsoft.playwright.*;
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

    @Value("${toss.id}")
    private String id;

    @Value("${toss.birthDay}")
    private String birthDay;

    @Value("${toss.phoneNumber}")
    private String phoneNumber;

    private final TossPortfolioCacheContainer container;

    public void generateTossPortfolio() {
        //Playwright 실행
        //active.profile=local일 경우 headless false
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isNotLocal))) {

            //저장된 StorageState Path 선언
            Path storagePath = Paths.get(chromeAuth);

            //저장된 StorageState가 존재하면 BrowserContext에 지정하고, 없으면 새로운 컨텍스트 생성
            BrowserContext context = Files.exists(storagePath) ? browser.newContext(new Browser.NewContextOptions()
                    .setStorageStatePath(storagePath)) : browser.newContext();

            Page page = context.newPage();

            //토스 증권 내 주식 전체보기 페이지로 이동
            page.navigate("https://tossinvest.com/investment-portfolio?product=all");

            //데이터 로딩 대기
            page.waitForTimeout(5000L);

            //아이디 Input 선택
            Locator idLocator = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[1]/div[1]/div/div/div/input");

            //아이디 Input이 보인다면 미로그인 상태
            if(idLocator.isVisible()) {
                //아이디 (이름) 선택 및 입력
                idLocator.fill(id);
                page.waitForTimeout(1000L);

                //생년월일 선택 및 입력
                Locator birthDayLocator = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[1]/div[2]/div/div/div/input");
                birthDayLocator.fill(birthDay);
                page.waitForTimeout(1000L);

                //전화번호 선택 및 입력
                Locator phoneNumberLocator = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[2]/div/div/div/input");
                phoneNumberLocator.fill(phoneNumber);
                page.waitForTimeout(1000L);

                //개인정보 관련 전체 동의 선택 및 클릭
                Locator checkAll = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/div[3]/div[1]/div/div/div/input");
                checkAll.click();
                page.waitForTimeout(1000L);

                //로그인 버튼 선택 및 클릭
                Locator loginBtn = page.locator("//html/body/div[1]/div/div/main/div/div[1]/div/div/div[3]/form/button");
                loginBtn.click();
                page.waitForTimeout(10000L);

                //로그인 유지 버튼 선택 및 클릭
                Locator keepLogin = page.locator("//html/body/div[4]/div[2]/div[3]/div[2]/button[2]");

                //이미 로그인 유지되는 디바이스일 경우 안보임
                if(keepLogin.isVisible()) {
                    keepLogin.click();
                    page.waitForTimeout(10000L);
                }

                //다시 토스 증권 내 주식 전체보기 페이지로 이동
                page.navigate("https://tossinvest.com/investment-portfolio?product=all");

                //데이터 로딩 대기
                page.waitForTimeout(5000L);
            }

            //MainSection Prefix
            String mainSection = "//*[@id=\"__next\"]/div/div[1]/main/main/section/section";

            //총 평가금액 선택 및 Text 가져오기
            Locator totalInvestmentLocator = page.locator(mainSection + "/div[1]/div[1]/div/span");
            String totalInvestment = totalInvestmentLocator.innerText();

            //총 투자금액 선택 및 Text 가져오기
            Locator originalInvestmentLocator = page.locator(mainSection + "/div[1]/div[2]/div[1]/div/span[2]");
            String originalInvestment = originalInvestmentLocator.innerText();

            //총 투자 수익률 선택 및 Text 가져오기
            Locator totalRevenueLocator = page.locator(mainSection + "/div[1]/div[2]/div[2]/div/span[2]");
            String totalRevenue = totalRevenueLocator.innerText();

            //당일 투자 수익률 선택 및 Text 가져오기
            Locator dailyRevenueLocator = page.locator(mainSection + "/div[1]/div[2]/div[3]/div/span[2]");
            String dailyRevenue = dailyRevenueLocator.innerText();

            //국내주식 평가금액 선택
            Locator domesticLocator = page.locator("//*[@id=\"all-tab\"]/section[1]/header/div/div/span[1]");

            //ex) 국내주식 · 322,200원
            String domestic = domesticLocator.innerText();
            String domesticTotal = domestic.split(" · ")[1];

            // 국내주식 수익률 선택
            Locator domesticRevenueLocator = page.locator("//*[@id=\"all-tab\"]/section[1]/header/div/div/span[2]/span");

            //ex) +3,600원 (1.1%)
            String domesticRevenue = domesticRevenueLocator.innerText();

            //국내주식 list 선택
            List<Locator> domesticLocators = page.locator("//*[@id=\"all-tab\"]/section[1]/div/div/div/div/table/tbody/tr").all();

            //국내주식 list header 선택
            Locator domesticHeaderTr = page.locator("//*[@id=\"all-tab\"]/section[1]/div/div/div/div/table/thead/tr");
            List<Locator> domesticHeaderThs = domesticHeaderTr.locator("th").all();

            List<Map<String, Object>> domesticsList = new ArrayList<>();

            for(int i = 1; i < domesticLocators.size(); i++) {
                Locator contentTr = domesticLocators.get(i);
                List<Locator> contentTds = contentTr.locator("td").all();

                Map<String, Object> domesticTable = IntStream.range(0, contentTds.size())
                        .mapToObj(j -> {
                            //한글로 된 헤더값들 영어 변수명으로 변경
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

            //해외주식 평가금액 선택
            Locator overSeasLocator = page.locator("//*[@id=\"all-tab\"]/section[2]/header/div/div/span[1]");

            //ex) 해외주식 · 1,666,504원
            String overSeas = overSeasLocator.innerText();
            String overSeasTotal = overSeas.split(" · ")[1];

            // 해외주식 수익률 선택
            Locator overSeasRevenueLocator = page.locator("//*[@id=\"all-tab\"]/section[2]/header/div/div/span[2]/span");

            //ex) -36,583원 (2.1%)
            String overSeasRevenue = overSeasRevenueLocator.innerText();

            //해외주식 list 선택
            List<Locator> overSeasLocators = page.locator("//*[@id=\"all-tab\"]/section[2]/div/div/div/div/table/tbody/tr").all();

            //해외주식 list header 선택
            Locator overSeasHeaderTr = page.locator("//*[@id=\"all-tab\"]/section[2]/div/div/div/div/table/thead/tr");
            List<Locator> overSeasHeaderThs = overSeasHeaderTr.locator("th").all();

            List<Map<String, Object>> overSeasList = new ArrayList<>();

            for(int i = 1; i < overSeasLocators.size(); i++) {
                Locator contentTr = overSeasLocators.get(i);
                List<Locator> contentTds = contentTr.locator("td").all();

                Map<String, Object> overSeasTable = IntStream.range(0, contentTds.size())
                        .mapToObj(j -> {
                            //한글로 된 헤더값들 영어 변수명으로 변경
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

            //컨테이너 저장
            container.setDto(dto);

            //StorageState 저장
            context.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(storagePath));
        }
    }

    public TossPortfolioDTO retrieveTossPortfolio() {
        return container.getDto();
    }
}
