package research.paper4.textmatch.impl.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import research.paper4.textmatch.enums.DocumentPart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;


@Service
@Slf4j
public class ParseService {

    private MistralAiChatModel mistralChatModel;

    @Autowired
    public void setMistralAiChatModel(MistralAiChatModel chatModel) {
        this.mistralChatModel = chatModel;
    }

    public Map<String, String> extractTextParts(String text, Boolean isResume, String initialText) {

        Map<String, String> mappedParts = mapTextParts(text);

        // case: check if you must resent prompt

        int regenerationCalls = 0;
        while (DocumentPart.values().length != mappedParts.size()) {
            log.error("The generated values size {} are NOT equal with the Document part values size {} resending prompt", DocumentPart.values().length, mappedParts.size());
            // regenerate content
            ChatResponse mistralResponse = mistralChatModel.call(PromptService.regenerateContent());
            regenerationCalls++;

            String answerText = mistralResponse.getResult().getOutput().getText();
            mappedParts = mapTextParts(answerText);
            // if regeneration fails for 2 times in a row do the process again
            if (DocumentPart.values().length != mappedParts.size() && regenerationCalls > 1) {
                log.info("RegenerationCalls greater than {} - Trying to generate content with initial text input", regenerationCalls);
                if(isResume){
                    mistralResponse = mistralChatModel.call(PromptService.enhanceAndStructureResume(initialText));
                } else {
                    mistralResponse = mistralChatModel.call(PromptService.enhanceAndStructureJobPost(initialText));
                }
                answerText = mistralResponse.getResult().getOutput().getText();
                mappedParts = mapTextParts(answerText);
            }

        }

        for (String key : mappedParts.keySet()) {
            // remove special characters
            String cleanText = mappedParts.get(key).replaceAll("[^a-zA-Z0-9.]", " ");
            // remove 2 or more spaces, replaces with one space.
            cleanText = cleanText.replaceAll("\\s{2,}", " ");
            mappedParts.replace(key, cleanText);
        }

        log.info("text response: {}", mappedParts);
        return mappedParts;
    }

    private Map<String, String> mapTextParts(String text) {

        String[] textParts = text.split(Pattern.quote("^-^"));
        Map<String, String> mappedParts = new LinkedHashMap<>();

        for (int i = 0; i < textParts.length; i++) {
            if (DocumentPart.isPartExist(textParts[i])) {
                String cleanedText = textParts[i + 1].trim();
                mappedParts.put(textParts[i], cleanedText);
            }

        }
        return mappedParts;
    }


}


