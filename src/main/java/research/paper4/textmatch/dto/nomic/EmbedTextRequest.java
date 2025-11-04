package research.paper4.textmatch.dto.nomic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmbedTextRequest {
    private Integer dimensions;
    private String inputs;
    private Boolean normalize = true;
    private Boolean truncate = false;
}
