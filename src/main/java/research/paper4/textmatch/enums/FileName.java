package research.paper4.textmatch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileName {
    JOB_POST_FILE("jobPostFile.xlsx"),
    RESUME_FILE("resumeFile.xlsx");

    private final String filename;
}

