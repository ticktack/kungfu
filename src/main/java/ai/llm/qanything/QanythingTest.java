package ai.llm.qanything;

import ai.llm.qanything.entity.AuthConfig;
import ai.llm.qanything.entity.ChatMessage;
import ai.llm.qanything.request.ChatRequest;
import ai.llm.qanything.response.StreamChatResponse;
import com.jfinal.kit.StrKit;
import io.reactivex.Flowable;

/**
 * @author yfq
 */
public class QanythingTest {
    private static final String APP_KEY = "your_app_key";     // 您的应用ID
    private static final String APP_SECRET = "your_app_secret";  // 您的应用密钥
    public static void main(String[] args) {
        QanythingClient client = new QanythingClient();
        String q = "your question";
        String kbId = "kb-20230306-12345678901234567890";
        // 设置认证信息
        AuthConfig authConfig = new AuthConfig(APP_KEY, APP_SECRET, q);
        ChatMessage message = new ChatMessage(kbId, q);
        ChatRequest request = new ChatRequest(message, authConfig);
        Flowable<StreamChatResponse> response = client.streamChat(request);
        response.doOnNext(s -> {
            if (StrKit.isBlank(s.getResult().getQuestion())) {
                if ( StrKit.notBlank(s.getResult().getResponse())) {
                    System.out.println(System.currentTimeMillis());
                    System.out.println(s.getResult().getResponse());
                }
            }
            else {
                System.out.println("来源：");
                System.out.println(s.getResult().getSource());
            }
        }).blockingSubscribe();
    }
}
