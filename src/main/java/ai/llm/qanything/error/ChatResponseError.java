package ai.llm.qanything.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ChatResponseError {
    
    private ChatResponseErrorDetail error;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatResponseErrorDetail{
        
        private String code;
        
        private String message;
        
        private String status;
    }
}
