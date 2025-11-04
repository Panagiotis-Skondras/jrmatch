package research.paper4.textmatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResumeRequest {
    @NotBlank(message = "Resume cannot be blank")
    private String resume;
}
