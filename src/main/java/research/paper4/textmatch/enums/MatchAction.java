package research.paper4.textmatch.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MatchAction {
    RESUME_TO_JOB_POSTS("res-to-job"),
    JOB_POST_TO_RESUMES("job-to-res"),
    CLEAR_CACHE_ALL("clear-all-cache"),
    CLEAR_CACHE_RESUMES("clear-resumes"),
    CLEAR_CACHE_JOB_POSTS("clear-job-posts"),
    FETCH_RESUME_CACHE_VALUES("fetch-res-cache"),
    FETCH_JOB_POST_CACHE_VALUES("fetch-job-post-cache"),
    FETCH_ALL_CACHE_KEYS("fetch-all-keys");

    private final String action;

    public static MatchAction valueToEnum(String action){
        return Arrays.stream(MatchAction.values())
                .filter(e -> e.getAction().equals(action))
                .findFirst().orElseThrow(() -> new RuntimeException("Input action - " + action + " - not exists to enum"));
    }

}
