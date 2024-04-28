package ai.llm.qanything.response;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *     "errorCode": "0",
 *     "msg": "SUCCESS",
 *     "requestId": "52095ec2-39ec-4eee-937d-b59967de1932",
 *     "result": {
 *         "question": "",
 *         "response": "split content...",
 *         "history": [
 *         ],
 *         "source": [
 *         ]
 *     }
 * }
 * @author yfq
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamChatResponse {
    private String errorCode;
    private String msg;
    private String requestId;
    private Result result;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private String question;
        private String response;
        private JSONArray history;
        private JSONArray source;
    }

}