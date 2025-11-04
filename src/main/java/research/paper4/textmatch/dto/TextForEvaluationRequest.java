package research.paper4.textmatch.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextForEvaluationRequest {
    private String text;
    private Boolean isResume;
}
