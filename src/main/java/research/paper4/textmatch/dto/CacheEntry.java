package research.paper4.textmatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CacheEntry {

    private Integer entryId;

    private Map<String, String> fieldsInString;

    @JsonIgnore
    private Map<String, float[]> gemmaFieldsInFloat;

    @JsonIgnore
    private Map<String, float[]> nomicFieldsInFloat;
}
