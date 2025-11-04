package research.paper4.textmatch.api;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import research.paper4.textmatch.dto.CommonResponse;
import research.paper4.textmatch.dto.JobPostRequest;
import research.paper4.textmatch.dto.MatchRequest;
import research.paper4.textmatch.dto.MatchResponse;
import research.paper4.textmatch.dto.ResumeRequest;
import research.paper4.textmatch.dto.TextForEvaluationRequest;

import java.util.List;

@RequestMapping("/document")
public interface DocumentApi {

    @PostMapping(value = "/process/jobPost", consumes = "application/json", produces = "application/json")
    ResponseEntity<CommonResponse> jobPostProcess(@Valid @RequestBody JobPostRequest req);

    @PostMapping(value = "/process/resume", consumes = "application/json", produces = "application/json")
    ResponseEntity<CommonResponse> resumeProcess(@Valid @RequestBody ResumeRequest req);

    @PostMapping(value = "/match", consumes = "application/json", produces = "application/json")
    ResponseEntity<List<MatchResponse>> matchProcess(@Valid @RequestBody MatchRequest req);

    @PostMapping(value = "/utils/cache", consumes = "application/json", produces = "application/json")
    ResponseEntity<CommonResponse> cacheUtils(@Valid @RequestBody MatchRequest req);

    @PostMapping(value = "/utils/create/excel", consumes = "application/json", produces = "application/json")
    ResponseEntity<CommonResponse> createExcelUtil(@Valid @RequestBody TextForEvaluationRequest req);

    @PostMapping(value = "/utils/system/evaluate", consumes = "application/json", produces = "application/json")
    ResponseEntity<CommonResponse> evaluateSystemUtil(@Valid @RequestBody TextForEvaluationRequest req);

}
