package ai.llm.qanything.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;


/**
 * @author yfq
 */
@Data
@AllArgsConstructor
public class ChatMessage {
    private List<String> kbIds;
    private String q;

    public ChatMessage(String kbId, String q) {
        this.kbIds = Collections.singletonList(kbId);
        this.q = q;
    }

}
