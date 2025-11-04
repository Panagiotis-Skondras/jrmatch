package research.paper4.textmatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, List<String>> allCacheKeys;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> cacheKeys;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Map<String, List<CacheEntry>>> resumeAllCacheEntries;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Map<String, List<CacheEntry>>> jobPostAllCacheEntries;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Map<String, Double>> evaluatedElements;
}
