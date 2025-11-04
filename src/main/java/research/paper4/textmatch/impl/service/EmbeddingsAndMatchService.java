package research.paper4.textmatch.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import research.paper4.textmatch.dto.nomic.EmbedTextRequest;
import research.paper4.textmatch.dto.nomic.NomicMatchSentenceRequest;

import java.util.List;

@Service
@Slf4j
public class EmbeddingsAndMatchService {

    @Value("${hf.nomic.similarity.uri}")
    private String nomicSimilarityUri;

    @Value("${hf.models.embed.uri}")
    private String embedUri;

    @Value("${hf.models.embed.dimensions}")
    private Integer embeddingsDimension;

    private WebClient embeddingsWebClient;
    @Autowired
    void setEmbeddingsWebClient(@Qualifier("embeddingsWebClient") WebClient embeddingsWebClient){
        this.embeddingsWebClient = embeddingsWebClient;
    }

    private WebClient gemmaEmbeddingsWebClient;
    @Autowired
    void setGemmaEmbeddingsWebClient(@Qualifier("embeddingsGemmaWebClient") WebClient gemmaEmbeddingsWebClient){
        this.gemmaEmbeddingsWebClient = gemmaEmbeddingsWebClient;
    }

    private EmbeddingModel embeddingModel;

    @Autowired
    public void setEmbeddingModel(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] mistralEmbeddings(String textPart){
         return embeddingModel.embed(textPart);
    }

    public float[] nomicEmbeddings(String textPart){
        return callNomicModelToCreateEmbeddings(textPart);
    }

    public float[] gemmaEmbeddings(String textPart){
        return callGemmaModelToCreateEmbeddings(textPart);
    }


    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private float[] callNomicModelToCreateEmbeddings(String text){

        // fill reqbody
        // send request capture response to float
        EmbedTextRequest reqBody = new EmbedTextRequest();
        reqBody.setDimensions(embeddingsDimension);
        reqBody.setInputs(text);

        List<List<Float>> response = embeddingsWebClient.method(HttpMethod.POST)
                .uri(embedUri)
                .bodyValue(reqBody).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<Float>>>() {})
                .block();

        float[] floatArray = new float[response.get(0).size()];
        for (int i = 0; i < response.get(0).size(); i++) {
            floatArray[i] = response.get(0).get(i);
        }

        return floatArray;

    }

    private float[] callGemmaModelToCreateEmbeddings(String text){

        // fill reqbody
        // send request capture response to float
        EmbedTextRequest reqBody = new EmbedTextRequest();
        reqBody.setDimensions(embeddingsDimension);
        reqBody.setInputs(text);

        List<List<Float>> response = gemmaEmbeddingsWebClient.method(HttpMethod.POST)
                .uri(embedUri)
                .bodyValue(reqBody).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<Float>>>() {})
                .block();

        float[] floatArray = new float[response.get(0).size()];
        for (int i = 0; i < response.get(0).size(); i++) {
            floatArray[i] = response.get(0).get(i);
        }

        return floatArray;

    }

    private String callNomicModel(List<String> texts, String uri){

        // fill reqbody
        // send request capture response to float
        NomicMatchSentenceRequest reqBody = new NomicMatchSentenceRequest();
        List<List<Double>> result = embeddingsWebClient.method(HttpMethod.POST)
                .uri(uri).bodyValue(reqBody).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<List<Double>>>() {})
                .block();

        return null;

    }


}
