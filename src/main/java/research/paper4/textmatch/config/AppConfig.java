package research.paper4.textmatch.config;


import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import research.paper4.textmatch.dto.CacheEntry;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.util.List;


@EnableCaching
@Configuration
public class AppConfig {

    @Value("${hf.api-key}")
    private String apiKey;

    @Value("${hf.nomic.embeddings.url}")
    private String nomicEmbeddingsUrl;

    @Value("${hf.gemma.embeddings.url}")
    private String gemmaEmbeddingsUrl;

    @Value("${hf.smollm2-1-7b.chat.url}")
    private String smollmChatUrl;

    @Value("${hf.qwen-3-1-7-b.chat.url}")
    private String qwenChatUrl;



    @Bean("resumesCache")
    Cache<String, List<CacheEntry>> resumesCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        MutableConfiguration<String, List<CacheEntry>> config =
                new MutableConfiguration<String, List<CacheEntry>>()
                        .setStoreByValue(false);

        return cacheManager.createCache("resumesCache", config);
    }

    @Bean("jobPostCache")
    Cache<String, List<CacheEntry>> jobPostCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        MutableConfiguration<String, List<CacheEntry>> config =
                new MutableConfiguration<String, List<CacheEntry>>()
                        .setStoreByValue(false);

        return cacheManager.createCache("jobPostCache", config);
    }

    @Bean("embeddingsWebClient")
    WebClient embeddingsWebClient(){
        return WebClient.builder().baseUrl(nomicEmbeddingsUrl).defaultHeaders(httpHeaders -> {
            httpHeaders.add("Accept", "application/json");
            httpHeaders.setBearerAuth(apiKey);
        }).build();
    }

    @Bean("embeddingsGemmaWebClient")
    WebClient embeddingsGemmaWebClient(){
        return WebClient.builder().baseUrl(gemmaEmbeddingsUrl).defaultHeaders(httpHeaders -> {
            httpHeaders.add("Accept", "application/json");
            httpHeaders.setBearerAuth(apiKey);
        }).build();
    }



    @Bean("smollmChatModel")
    HuggingfaceChatModel smollmChatModel(){
        return new HuggingfaceChatModel(apiKey, smollmChatUrl);
    }

    @Bean("qwenChatModel")
    HuggingfaceChatModel qwenChatModel(){
        return new HuggingfaceChatModel(apiKey, qwenChatUrl);
    }
}
