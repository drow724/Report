package com.report.container;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class OpenAiCacheContainer {

    private String openAiMarkUpMessage = "";

    synchronized public void setOpenAiMarkUpMessage(String openAiMarkUpMessage) {
        this.openAiMarkUpMessage = openAiMarkUpMessage;
    }
}
