package research.paper4.textmatch.impl.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import research.paper4.textmatch.api.DocumentApi;
import research.paper4.textmatch.dto.CacheEntry;
import research.paper4.textmatch.dto.CommonResponse;
import research.paper4.textmatch.dto.internal.EntryForEvaluation;
import research.paper4.textmatch.dto.JobPostRequest;
import research.paper4.textmatch.dto.MatchRequest;
import research.paper4.textmatch.dto.MatchResponse;
import research.paper4.textmatch.dto.ResumeRequest;
import research.paper4.textmatch.dto.TextForEvaluationRequest;
import research.paper4.textmatch.enums.FileName;
import research.paper4.textmatch.enums.MatchAction;
import research.paper4.textmatch.impl.service.CacheService;
import research.paper4.textmatch.impl.service.EmbeddingsAndMatchService;
import research.paper4.textmatch.impl.service.ExcelService;
import research.paper4.textmatch.impl.service.ParseService;
import research.paper4.textmatch.impl.service.PromptService;
import research.paper4.textmatch.util.DocumentApiImplHelper;
import research.paper4.textmatch.util.MatchUtils;
import javax.cache.Cache;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@Slf4j
public class DocumentApiImpl implements DocumentApi {

    private MistralAiChatModel mistralChatModel;
    private Cache<String, List<CacheEntry>> jobPostCache;
    private Cache<String, List<CacheEntry>> resumesCache;

    @Autowired
    public void setJobPostCache(@Qualifier("jobPostCache") Cache<String, List<CacheEntry>> jobPostCache) {
        this.jobPostCache = jobPostCache;
    }

    @Autowired
    public void setResumesCache(@Qualifier("resumesCache") Cache<String, List<CacheEntry>> resumesCache) {
        this.resumesCache = resumesCache;
    }

    @Autowired
    public void setMistralAiChatModel(MistralAiChatModel chatModel) {
        this.mistralChatModel = chatModel;
    }

    private ParseService parseService;

    @Autowired
    void setParseService(ParseService parseService) {
        this.parseService = parseService;
    }

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

    private ExcelService excelService;
    @Autowired
    void setExcelService(ExcelService excelService){
        this.excelService = excelService;
    }

    private DocumentApiImplHelper apiHelper;
    @Autowired
    void setApiHelper(DocumentApiImplHelper apiHelper){
        this.apiHelper = apiHelper;
    }


    @Override
    public ResponseEntity<CommonResponse> jobPostProcess(JobPostRequest req) {
        String[] jobTitle = apiHelper.processText(false, req.getJobPost()).split("-");
        return ResponseEntity.ok(new CommonResponse(jobTitle[1], null, Set.of(jobTitle[0]), null, null, null));

    }

    @Override
    public ResponseEntity<CommonResponse> resumeProcess(ResumeRequest req) {
        String[] resumeTitle = apiHelper.processText(true, req.getResume()).split("-");
        return ResponseEntity.ok(new CommonResponse(resumeTitle[1], null, Set.of(resumeTitle[0]), null, null, null));
    }

    @Override
    public ResponseEntity<List<MatchResponse>> matchProcess(MatchRequest req) {
        if (req.getAction().equals(MatchAction.RESUME_TO_JOB_POSTS.getAction())) {
            log.info("Resume keys: {} - Job posts keys: {}", resumesCache.iterator().next().getKey(), jobPostCache.iterator().next().getKey());

            // get job posts that titles are similar e.g. software engineer - software developer
            Map<String, List<CacheEntry>> jobPostEntries = new HashMap<>();
            for(String jobPostKey : req.getJobPostKeys()){
                jobPostEntries.put(jobPostKey, jobPostCache.get(jobPostKey));
            }

            // case: resume cache has one entry
            String inputResumeKey = req.getResumeKeys().stream().reduce((a,b)->b).orElseThrow(() -> new RuntimeException("No resume key found"));
            List<CacheEntry> resumes = resumesCache.get(inputResumeKey);

            if (resumes.size() > 1) {
                throw new RuntimeException("This action matches ONLY 1 resume with multiple job posts");
            }
            CacheEntry resume = resumes.get(0);
            List<MatchResponse> r = MatchUtils.calculateSimilarity(jobPostEntries, resume, req.getAction(), true);
            List<Map<String, String>> excelDataList = apiHelper.buildDataToCreateExcel(r);
            excelService.createResultsExcel("resume-to-jobPosts.xlsx", excelDataList);

            return ResponseEntity.ok(r);

        } else if (req.getAction().equals(MatchAction.JOB_POST_TO_RESUMES.getAction())){
            log.info("Job post keys: {} - Resume keys: {}", jobPostCache.iterator().next().getKey(), resumesCache.iterator().next().getKey());

            // get resumes that titles are similar e.g. software engineer - software developer
            Map<String, List<CacheEntry>> resumeEntries = new HashMap<>();
            for(String resumeKey : req.getResumeKeys()){
                resumeEntries.put(resumeKey, resumesCache.get(resumeKey));
            }

            // case: job post cache has one entry
            String inputJoPostKey = req.getJobPostKeys().stream().reduce((a,b)->b).orElseThrow(() -> new RuntimeException("No resume key found"));
            List<CacheEntry> jobPosts = jobPostCache.get(inputJoPostKey);

            if (jobPosts.size() > 1) {
                throw new RuntimeException("This action matches ONLY 1 job post with multiple resumes");
            }
            CacheEntry jobPost = jobPosts.get(0);
            List<MatchResponse> r = MatchUtils.calculateSimilarity(resumeEntries, jobPost, req.getAction(), false);
            List<Map<String, String>> excelDataList = apiHelper.buildDataToCreateExcel(r);
            excelService.createResultsExcel("jobPost-to-resume.xlsx", excelDataList);

            return ResponseEntity.ok(r);
        }
        else {
            log.info("Action {} NOT recognized", req.getAction());
            MatchResponse r = new MatchResponse();
            r.setMatchAction(null);
            return ResponseEntity.ok(List.of(r));
        }
    }


    @Override
    public ResponseEntity<CommonResponse> cacheUtils(MatchRequest req) {
        return ResponseEntity.ok(cacheService.executeCacheAction(req));
    }

    @Override
    public ResponseEntity<CommonResponse> createExcelUtil(TextForEvaluationRequest req) {

        Prompt enhanceAndStructurePrompt;
        if(req.getIsResume()){
            enhanceAndStructurePrompt = PromptService.enhanceAndStructureResume(req.getText());
        } else {
            enhanceAndStructurePrompt = PromptService.enhanceAndStructureJobPost(req.getText());
        }

        ChatResponse mistralResponse = mistralChatModel.call(enhanceAndStructurePrompt);

        String answerText = mistralResponse.getResult().getOutput().getText();

        Map<String, String> textParts = parseService.extractTextParts(answerText, req.getIsResume(), req.getText());
        textParts.put("INPUT_FULL_TEXT", req.getText());

        if(req.getIsResume()){
            excelService.createExcel(FileName.RESUME_FILE.getFilename(), textParts);
        } else {
            excelService.createExcel(FileName.JOB_POST_FILE.getFilename(), textParts);
        }
        CommonResponse c = new CommonResponse();
        c.setMessage("Operation completed successfully");
        return ResponseEntity.ok(c);
    }

    @Override
    public ResponseEntity<CommonResponse> evaluateSystemUtil(TextForEvaluationRequest req) {

        List<Map<String, String>> listElements;

        // read all entries from excel resumes or job posts
        if(req.getIsResume()){
            listElements = excelService.readExcel(FileName.RESUME_FILE.getFilename());
        } else{
            listElements = excelService.readExcel(FileName.JOB_POST_FILE.getFilename());
        }

        List<EntryForEvaluation> entriesToBeEvaluated = new ArrayList<>();

        // structure and calculate for every resume or jop post the embeddings
        for(Map<String, String> element : listElements){

            String fullText = element.get("INPUT_FULL_TEXT");
            Prompt enhanceAndStructurePrompt;
            if(req.getIsResume()){
                enhanceAndStructurePrompt = PromptService.enhanceAndStructureResume(fullText);
            } else {
                enhanceAndStructurePrompt = PromptService.enhanceAndStructureJobPost(fullText);
            }

            ChatResponse mistralResponse = mistralChatModel.call(enhanceAndStructurePrompt);
            String answerText = mistralResponse.getResult().getOutput().getText();
            Map<String, String> textParts = parseService.extractTextParts(answerText, req.getIsResume(), fullText);

            Map<String, float[]> gemmaEmbedPartsForValidation = new LinkedHashMap<>();
            Map<String, float[]> nomicEmbedPartsForValidation = new LinkedHashMap<>();

            for (String key : textParts.keySet()) {
                String textPart = textParts.get(key);
                gemmaEmbedPartsForValidation.put(key, embeddingsAndMatchService.gemmaEmbeddings(textPart));
                nomicEmbedPartsForValidation.put(key,embeddingsAndMatchService.nomicEmbeddings(textPart));
            }

            Map<String, float[]> gemmaTextValidEmbedParts = new LinkedHashMap<>();
            Map<String, float[]> nomicTextValidEmbedParts = new LinkedHashMap<>();

            // remove key - value because they will be used to cosine similarity
            element.remove("INPUT_FULL_TEXT");
            for (String key : element.keySet()) {
                String textPart = element.get(key);
                gemmaTextValidEmbedParts.put(key, embeddingsAndMatchService.gemmaEmbeddings(textPart));
                nomicTextValidEmbedParts.put(key,embeddingsAndMatchService.nomicEmbeddings(textPart));
            }

            // a job post or resume
            EntryForEvaluation e = new EntryForEvaluation(element, nomicTextValidEmbedParts, nomicEmbedPartsForValidation,
                                                                    gemmaTextValidEmbedParts, gemmaEmbedPartsForValidation);
            entriesToBeEvaluated.add(e);
        }
        List<Map<String, Double>> allEvaluatedElements = new ArrayList<>();
        for(EntryForEvaluation element : entriesToBeEvaluated){
            Map<String, Double> elementEvaluation = new LinkedHashMap<>();
            Double nomicSum = 0.0;
            Double gemmaSum = 0.0;
            for(String key : element.getValidTextEntry().keySet()){
                float[] validNomicEmbeddings = element.getValidNomicTextEntryEmbeddings().get(key);
                float[] nomicToBeValidatedEmbeddings = element.getGeneratedNomicTextEntryEmbeddings().get(key);
                double nomicResult = EmbeddingsAndMatchService.cosineSimilarity(validNomicEmbeddings, nomicToBeValidatedEmbeddings);
                BigDecimal nomicScoreWith2Decimal = new BigDecimal(nomicResult).setScale(2, RoundingMode.HALF_UP);
                elementEvaluation.put("NOMIC-".concat(key), nomicScoreWith2Decimal.doubleValue());
                nomicSum = nomicSum + nomicScoreWith2Decimal.doubleValue();

                float[] validGemmaEmbeddings = element.getValidGemmaTextEntryEmbeddings().get(key);
                float[] gemmaToBeValidatedEmbeddings = element.getGeneratedGemmaTextEntryEmbeddings().get(key);
                double gemmaResult = EmbeddingsAndMatchService.cosineSimilarity(validGemmaEmbeddings, gemmaToBeValidatedEmbeddings);
                BigDecimal gemmaScoreWith2Decimal = new BigDecimal(gemmaResult).setScale(2, RoundingMode.HALF_UP);
                elementEvaluation.put("GEMMA-".concat(key), gemmaScoreWith2Decimal.doubleValue());
                gemmaSum = gemmaSum + gemmaScoreWith2Decimal.doubleValue();
            }

            double nomicAverage = nomicSum/element.getValidNomicTextEntryEmbeddings().size();
            elementEvaluation.put("NOMIC-AVERAGE", new BigDecimal(nomicAverage).setScale(2, RoundingMode.HALF_UP).doubleValue());

            double gemmaAverage = gemmaSum/element.getValidGemmaTextEntryEmbeddings().size();
            elementEvaluation.put("GEMMA-AVERAGE", new BigDecimal(gemmaAverage).setScale(2, RoundingMode.HALF_UP).doubleValue());

            allEvaluatedElements.add(elementEvaluation);
        }

        CommonResponse c = new CommonResponse();
        c.setMessage("Operation completed successfully");
        c.setEvaluatedElements(allEvaluatedElements);
        return ResponseEntity.ok(c);
    }
}
