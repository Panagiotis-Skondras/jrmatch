package research.paper4.textmatch.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import research.paper4.textmatch.dto.CacheEntry;
import research.paper4.textmatch.dto.MatchResponse;
import research.paper4.textmatch.enums.DocumentPart;
import research.paper4.textmatch.impl.service.CacheService;
import research.paper4.textmatch.impl.service.EmbeddingsAndMatchService;
import research.paper4.textmatch.impl.service.ParseService;
import research.paper4.textmatch.impl.service.PromptService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class DocumentApiImplHelper {

    private MistralAiChatModel mistralChatModel;


    @Autowired
    public void setMistralAiChatModel(MistralAiChatModel chatModel) {
        this.mistralChatModel = chatModel;
    }

    private ParseService parseService;

    private EmbeddingsAndMatchService embeddingsAndMatchService;

    @Autowired
    void setEmbeddingsService(EmbeddingsAndMatchService embeddingsAndMatchService) {
        this.embeddingsAndMatchService = embeddingsAndMatchService;
    }

    private CacheService cacheService;

    @Autowired
    void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Autowired
    void setParseService(ParseService parseService) {
        this.parseService = parseService;
    }


    public String processText(Boolean isResume, String text){

        Prompt enhanceAndStructureTextPrompt;
        if(isResume){
            enhanceAndStructureTextPrompt = PromptService.enhanceAndStructureResume(text);
        } else {
            enhanceAndStructureTextPrompt = PromptService.enhanceAndStructureJobPost(text);
        }

        ChatResponse mistralResponse = mistralChatModel.call(enhanceAndStructureTextPrompt);

        String answerText = mistralResponse.getResult().getOutput().getText();

        Map<String, String> textParts = parseService.extractTextParts(answerText, isResume, text);

        Map<String, float[]> gemmaTextEmbedParts = new LinkedHashMap<>();
        Map<String, float[]> nomicTextEmbedParts = new LinkedHashMap<>();

        for (String key : textParts.keySet()) {
            String textPart = textParts.get(key);
            gemmaTextEmbedParts.put(key, embeddingsAndMatchService.gemmaEmbeddings(textPart));
            nomicTextEmbedParts.put(key, embeddingsAndMatchService.nomicEmbeddings(textPart));
        }

        CacheEntry entry = new CacheEntry();
        entry.setEntryId(ThreadLocalRandom.current().nextInt(1000));
        entry.setFieldsInString(textParts);
        entry.setGemmaFieldsInFloat(gemmaTextEmbedParts);
        entry.setNomicFieldsInFloat(nomicTextEmbedParts);

        String textTitle = textParts.get(DocumentPart.TITLE.name());

        if(isResume){
            cacheService.addResumeEntry(textTitle, entry);
        } else {
            cacheService.addJobPostEntry(textTitle, entry);
        }

        log.info("Is resume {} - Added to cache - title {} - entry {}", isResume, textTitle, entry);
        return textTitle + "-" + entry.getEntryId().toString();
    }

    public List<Map<String, String>> buildDataToCreateExcel(List<MatchResponse> r) {
        List<Map<String, String>> excelDataList = new ArrayList<>();
        for (MatchResponse m : r) {
            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("MatchAction", m.getMatchAction().name());
            dataMap.put("ResumeID", m.getResumeId().toString());
            dataMap.put("ResumeTitle", m.getResumeTitle());
            dataMap.put("JobPostId", m.getJobPostId().toString());
            dataMap.put("JobPostTitle", m.getJobPostTitle());
            dataMap.put("G-Overall", m.getGemmaOverallMatchScore().toString());
            dataMap.put("N-Overall", m.getNomicOverallMatchScore().toString());
            dataMap.put("G-Title", m.getGemmaTitleScore().toString());
            dataMap.put("N-Title", m.getNomicTitleScore().toString());
            dataMap.put("G-Desc", m.getGemmaDescriptionScore().toString());
            dataMap.put("N-Desc", m.getNomicDescriptionScore().toString());
            dataMap.put("G-Resp", m.getGemmaResponsibilitiesScore().toString());
            dataMap.put("N-Resp", m.getNomicResponsibilitiesScore().toString());
            dataMap.put("G-Edu", m.getGemmaEducationScore().toString());
            dataMap.put("N-Edu", m.getNomicEducationScore().toString());
            dataMap.put("G-Exp", m.getGemmaExperienceScore().toString());
            dataMap.put("N-Exp", m.getNomicExperienceScore().toString());
            dataMap.put("G-Skills", m.getGemmaSkillsScore().toString());
            dataMap.put("N-Skills", m.getNomicSkillsScore().toString());
            dataMap.put("G-AddInfo", m.getGemmaAdditionalInfoScore().toString());
            dataMap.put("N-AddInfo", m.getNomicAdditionalInfoScore().toString());

            excelDataList.add(dataMap);
        }

        return excelDataList;
    }
}
