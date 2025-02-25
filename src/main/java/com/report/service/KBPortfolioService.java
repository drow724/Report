package com.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.report.container.KBPortfolioCacheContainer;
import com.report.dto.KBPortfolioDTO;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KBPortfolioService {

    private final KBPortfolioCacheContainer container;

    private final ObjectMapper mapper;

    public void keepSession() {
        final RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = getHeaders();

        ResponseEntity<String> entity = restTemplate.exchange("https://obank.kbstar.com/quics?asfilecode=548634&QSL=F", HttpMethod.POST, new HttpEntity<>(headers), String.class);
        HttpHeaders responseHeader = entity.getHeaders();

        List<String> cookies = Optional.ofNullable(responseHeader.get("Set-Cookie")).orElseGet(ArrayList::new);

        for(String cookie : cookies) {
            String[] str = cookie.split("=");
            if("JSESSIONID".equals(str[0])) {
                container.setJsessionId(str[1]);
                continue;
            }

            if("QSID".equals(str[0])) {
                container.setQsid(str[1]);
            }
        }
    }

    public void generateKBPortfolio() {
        final RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = getHeaders();

        ResponseEntity<String> entity = restTemplate.exchange("https://obank.kbstar.com/quics?page=C055069", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        HttpHeaders responseHeader = entity.getHeaders();

        List<String> cookies = Optional.ofNullable(responseHeader.get("set-cookie")).orElseGet(ArrayList::new);

        for(String cookie : cookies) {
            String[] str = cookie.split("=");
            if("JSESSIONID".equals(str[0])) {
                container.setJsessionId(str[1]);
                continue;
            }
            if("QSID".equals(str[0])) {
                container.setQsid(str[1]);
            }
        }

        String responseBody = Optional.ofNullable(entity.getBody()).orElse("");
        Document doc = Jsoup.parse(responseBody);

        Optional<Element> portfolioElement = Optional.ofNullable(doc.selectFirst("#IBF > div.ui-togglecon.prtflo"));

        KBPortfolioDTO.KBPortfolioDTOBuilder builder = KBPortfolioDTO.builder();

        if(portfolioElement.isPresent()) {
            Element element = portfolioElement.get();
            Elements elements = element.children();
            if(!elements.isEmpty()) {
                Element totalElement = elements.get(0);

                Elements totalElementChildren = totalElement.children();

                if(!totalElementChildren.isEmpty()) {
                    Element bankAccountElement = totalElementChildren.get(0);
                    String bankAccount = bankAccountElement.text();
                    builder.bankAccount(bankAccount);

                    Element portfolioTitleElement = totalElementChildren.size() > 1 ? totalElementChildren.get(1) : new Element("h3");
                    String portfolioTitle = portfolioTitleElement.text();
                    builder.portfolioTitle(portfolioTitle);

                    Element portfolioInfoElement = totalElementChildren.size() > 2 ? totalElementChildren.get(2) : new Element("ul");

                    Elements portfolioInfoChildren = portfolioInfoElement.children();

                    if(portfolioInfoChildren.size() > 1) {
                        Element totalInvestmentElementWrapper = portfolioInfoChildren.get(1);
                        Elements totalInvestmentElements = totalInvestmentElementWrapper.children();

                        if(!totalInvestmentElements.isEmpty()) {
                            Element totalInvestmentElement = totalInvestmentElements.get(0);
                            String totalInvestment = totalInvestmentElement.text();
                            builder.totalInvestment(totalInvestment);

                            Element totalRevenueElement = totalInvestmentElements.size() > 1 ? totalInvestmentElements.get(1) : new Element("span");

                            Elements totalRevenueElements = totalRevenueElement.children();

                            String totalRevenue = totalRevenueElement.text();
                            builder.totalRevenue(totalRevenue);

                            if(!totalRevenueElements.isEmpty()) {
                                Element isPositiveElement = totalRevenueElements.get(0);
                                Boolean isPositive = "상승".equals(isPositiveElement.text().trim());
                                builder.isPositive(isPositive);
                            }
                        }
                    }
                }
            }
        }

        Optional<Element> portfolioDetailElement = Optional.ofNullable(doc.selectFirst("#포트폴리오목록_0000339270 > div > ul"));

        if(portfolioDetailElement.isPresent()) {
            Element element = portfolioDetailElement.get();
            Elements elements = element.children();
            for(Element detailUl : elements) {
                Elements detailLis = detailUl.children();

                for(Element detailDiv : detailLis) {
                    //Elements detailChildren = detailChild.children();
                }

            }
        }
    }

    public KBPortfolioDTO retrieveKBPortfolio() {
        return container.getDto();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "JSESSIONID=" + container.getJsessionId());
        headers.add("Cookie", "_xm_webid_1_=794138549");
        headers.add("Cookie", "WMONID=sGCm9jCRPQh");
        headers.add("Cookie", "wizinhips_tracer_cid=bidd5ee9a2488ef4f0563e73851de706d5b06444f13");
        headers.add("Cookie", "TRACER_JSESSIONID=0000r6MR5TSqp2JWqDL4bCNripW:WIZ20201");
        headers.add("Cookie", "m_sid=%7C1740451375453");
        headers.add("Cookie", "m_s_start=1740451375453");
        headers.add("Cookie", "_m_uid=8ced3fc1-eed1-37a2-823f-2e3dca96c38a");
        headers.add("Cookie", "_m_uidt=S");
        headers.add("Cookie", "_m_uid_type=A");
        headers.add("Cookie", "_M_CS[T]=1");
        headers.add("Cookie", "_LOG_VSTRIDNFIVAL=KD0/u8eHQTKHCLCJ0jktBQ");
        headers.add("Cookie", "LOG_NEWCONNDSTIC=Y");
        headers.add("Cookie", "bwCkVal=20250225114304442");
        headers.add("Cookie", "QSID=" + container.getQsid());
        headers.add("Cookie", "_ga=GA1.2.1668668342.1740451388");
        headers.add("Cookie", "_gid=GA1.2.894809670.1740451388");
        headers.add("Cookie", "PplrCookies=");
        headers.add("Cookie", "kpdCd=103");
        headers.add("Cookie", "wizinhips_tracer_bid=bidd5ee9a2488ef4f0563e73851de706d5b06444f13");
        headers.add("Cookie", "_KB_N_TIKER=");
        headers.add("Cookie", "_pk_id.SER0000001.ce94=ad5389f6ddda502a.1740451388.0.1740451515..");
        headers.add("Cookie", "delfino.recentModule=G3");
        headers.add("Cookie", "_ga_PWF0L3XHV4=GS1.2.1740451388.1.1.1740451514.0.0.0");
        headers.add("Cookie", "_ga_XTXVQQ6FS8=GS1.2.1740451388.1.1.1740451514.0.0.0");
        headers.add("Cookie", "_ga_TC7KKN0QLR=GS1.2.1740451388.1.1.1740451514.0.0.0");

        return headers;
    }
}
