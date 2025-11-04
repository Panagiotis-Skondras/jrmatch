package research.paper4.textmatch.dto;

import lombok.Getter;
import lombok.Setter;
import research.paper4.textmatch.enums.MatchAction;

import java.util.Comparator;
import java.util.Map;

@Getter
@Setter
public class MatchResponse {
    private MatchAction matchAction;
    private Integer resumeId;
    private String resumeTitle;
    private Integer jobPostId;
    private String jobPostTitle;

    private Double gemmaOverallMatchScore;
    private Double nomicOverallMatchScore;

    private Double gemmaTitleScore;
    private Double nomicTitleScore;

    private Double gemmaDescriptionScore;
    private Double nomicDescriptionScore;

    private Double gemmaResponsibilitiesScore;
    private Double nomicResponsibilitiesScore;

    private Double gemmaEducationScore;
    private Double nomicEducationScore;

    private Double gemmaExperienceScore;
    private Double nomicExperienceScore;

    private Double gemmaSkillsScore;
    private Double nomicSkillsScore;

    private Double gemmaAdditionalInfoScore;
    private Double nomicAdditionalInfoScore;

}
