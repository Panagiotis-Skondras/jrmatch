package research.paper4.textmatch.impl.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TextGenerationService {

    private HuggingfaceChatModel qwenChat;
    @Autowired
    void setQwenChat(@Qualifier("qwenChatModel") HuggingfaceChatModel qwenChat){
        this.qwenChat = qwenChat;
    }

    private HuggingfaceChatModel smollmChat;
    @Autowired
    void setSmollmChat(@Qualifier("smollmChatModel") HuggingfaceChatModel smollmChatModel){
        this.smollmChat = smollmChatModel;
    }


    public void callQwenModel(Prompt prompt){
        Generation response = qwenChat.call(prompt).getResult();
        log.info("Qwen generation {}", response);

    }

    public void callSmollmModel(Prompt prompt){
        Generation response = smollmChat.call(prompt).getResult();
        log.info("Smollm generation {}", response);
    }


}
