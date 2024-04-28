package ai.llm.qanything.exception;

import ai.llm.qanything.error.ChatResponseError;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AiSdkException extends RuntimeException implements Serializable {

    private String code;

    private String msg;
    
    private List<ChatResponseError> details;
    
    public AiSdkException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public AiSdkException(String code, String msg, List<ChatResponseError> details) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.details = details;
    }
}
