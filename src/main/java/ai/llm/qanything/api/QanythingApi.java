package ai.llm.qanything.api;

import ai.llm.qanything.request.ChatRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * @author yfq
 * @date 2024-04-26
 */
public interface QanythingApi {

    @Streaming
    @POST("q_anything/paas/chat_stream")
    Call<ResponseBody> streamChat(@Body ChatRequest request);

    //@HeaderMap Map<String, String> headers,
}
