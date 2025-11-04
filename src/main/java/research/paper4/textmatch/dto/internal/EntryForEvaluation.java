package research.paper4.textmatch.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class EntryForEvaluation {

    private Map<String, String> validTextEntry;
    private Map<String, float[]> validNomicTextEntryEmbeddings;
    private Map<String, float[]> generatedNomicTextEntryEmbeddings;
    private Map<String, float[]> validGemmaTextEntryEmbeddings;
    private Map<String, float[]> generatedGemmaTextEntryEmbeddings;
}
