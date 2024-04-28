package ai.llm.qanything;

import ai.llm.qanything.api.QanythingApi;
import ai.llm.qanything.entity.ResponseBodyCallback;
import ai.llm.qanything.entity.SSE;
import ai.llm.qanything.interceptor.QanythingAuthenticationInterceptor;
import ai.llm.qanything.request.ChatRequest;
import ai.llm.qanything.response.StreamChatResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yfq
 * @date 2024-04-26
 */
public class QanythingClient {
    private static final String BASE_URL = "https://openapi.youdao.com";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = defaultObjectMapper();


    private final QanythingApi api;
    private final ExecutorService executorService;

    public QanythingClient() {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(DEFAULT_TIMEOUT);
        Retrofit retrofit = defaultRetrofit(client, mapper, null);

        this.api = retrofit.create(QanythingApi.class);
        this.executorService = client.dispatcher().executorService();
    }

    public static OkHttpClient defaultClient(Duration timeout) {
        return new OkHttpClient.Builder()
                .addInterceptor(new QanythingAuthenticationInterceptor())
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper, String baseUrl) {
        if (baseUrl == null || "".equals(baseUrl)) {
            baseUrl = BASE_URL;
        }
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

    public Flowable<StreamChatResponse> streamChat(ChatRequest request) {
        return stream(api.streamChat(request), StreamChatResponse.class);
    }

    public static <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> cl) {
        return stream(apiCall).map(sse -> {
            if (sse.getData() == null || "".equals(sse.getData())) {
                return null;
            } else {
                return mapper.readValue(sse.getData(), cl);
            }
        });
    }

    public static Flowable<SSE> stream(Call<ResponseBody> apiCall) {
        return stream(apiCall, false);
    }

    public static Flowable<SSE> stream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone)), BackpressureStrategy.BUFFER);
    }

}
