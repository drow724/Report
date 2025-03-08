package com.report.service;

import com.report.container.OpenAiCacheContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    private final OpenAiCacheContainer openAiCacheContainer;

    @Value("${report.openai.template}")
    private String template;

    public void generateOpenAi(String portfolio) {
        PromptTemplate promptTemplate = new PromptTemplate(template);

        //portfolio 플레이스 홀더 json으로 치환
        promptTemplate.add("portfolio", portfolio);

        //프롬프트 생성
        Prompt prompt = promptTemplate.create();

        //chatGpt api 호출
        ChatResponse chatResponse = openAiChatModel.call(prompt);

        //결과값 꺼내기
        Generation results = chatResponse.getResult();
        AssistantMessage assistantMessage = results.getOutput();
        String text = assistantMessage.getText();

        //컨테이너 저장
        openAiCacheContainer.setOpenAiMarkUpMessage(text);
    }

    public String retrieveOpenAiMarkUpMessage() {
        return openAiCacheContainer.getOpenAiMarkUpMessage();
    }
}
