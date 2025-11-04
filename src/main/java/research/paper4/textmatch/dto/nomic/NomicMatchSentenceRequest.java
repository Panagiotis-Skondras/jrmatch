package research.paper4.textmatch.dto.nomic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class NomicMatchSentenceRequest {
    public Inputs inputs;
    public String parameters = null;

    @Getter
    @Setter
    @ToString
    public static class Inputs {
        public List<String> sentences;
        @JsonProperty("source_sentence")
        public String sourceSentence;
    }
}
