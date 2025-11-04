package research.paper4.textmatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class MatchRequest {
    @NotBlank
    private String action;
    private Set<String> resumeKeys;
    private Set<String> jobPostKeys;
}
