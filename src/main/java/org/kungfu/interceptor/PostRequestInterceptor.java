package org.kungfu.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import org.kungfu.core.KungfuConstant;

public class PostRequestInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String method = controller.getRequest().getMethod();
        if (method.equals(KungfuConstant.HTTP_METHOD_POST)) {
            String jsonRequest = HttpKit.readData(controller.getRequest());
            // 参数传递
            inv.getController().set(KungfuConstant.JSON_REQUEST_BODY, jsonRequest);
        }

        inv.invoke();
    }
}
