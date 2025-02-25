package com.report.service;

import com.report.constants.CacheType;
import com.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiChatModel openAiChatModel;

    @Value("${report.openai.template}")
    private String template;

    public void retrieveOpenAi(String portfolio) {
        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("portfolio", portfolio);

        Prompt prompt = promptTemplate.create();
        ChatResponse chatResponse = openAiChatModel.call(prompt);

        List<Generation> results = chatResponse.getResults();
    }
}
