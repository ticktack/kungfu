package ai.llm.qanything.request;

import ai.llm.qanything.entity.AuthConfig;
import ai.llm.qanything.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * {
 *     "kbIds": [
 *         "KB46d7f879227f44a89af876fdf89b7a72_240328"
 *     ],
 *     "q": "gemini有什么优势？",
 *     "curtime": "1714124942",
 *     "salt": "bfc978e3-f789-45f5-9908-29a4fe348b64",
 *     "sign": "a370319536352e9f1f52048bf40dbb7073fec5b751c15b5b83000234e7cbab4e",
 *     "signType": "v3",
 *     "appKey": "2f7d01bd035b4eba"
 * }
 *
 * @author yfq
 */
@Data
@Builder
@AllArgsConstructor
public class ChatRequest {
    private List<String> kbIds;
    private String q;
    private String curtime;
    private String salt;
    private String signType;
    private String sign;
    private String appKey;

    public ChatRequest(ChatMessage message, AuthConfig authConfig) {
        this.kbIds = message.getKbIds();
        this.q = message.getQ();
        this.curtime = authConfig.getCurtime();
        this.salt = authConfig.getSalt();
        this.signType = authConfig.getSignType();
        this.sign = authConfig.getSign();
        this.appKey = authConfig.getAppKey();
    }

}