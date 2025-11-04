package research.paper4.textmatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JobPostRequest {
    @NotBlank(message = "Job post cannot be blank")
    private String jobPost;
}
