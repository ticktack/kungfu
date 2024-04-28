package ai.llm.qanything.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class QanythingAuthenticationInterceptor implements Interceptor {
    public QanythingAuthenticationInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .header("content-type", "application/json")
                .build();
        return chain.proceed(request);
    }
}
