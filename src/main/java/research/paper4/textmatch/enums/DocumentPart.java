package research.paper4.textmatch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DocumentPart {
    TITLE,
    DESCRIPTION,
    RESPONSIBILITIES,
    EDUCATION,
    SKILLS,
    EXPERIENCE,
    ADDITIONAL_INFO;

    public static boolean isPartExist(String partName){
        return Arrays.stream(DocumentPart.values())
                .anyMatch(dp -> dp.name().equals(partName));
    }

}
