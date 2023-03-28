package org.kungfu.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import org.kungfu.util.TokenKit;

public class AuthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		Controller controller = inv.getController();

		if (controller.getViewPath().contains("/swagger/")) {
			inv.invoke();
		}
		// 方便本地调试
		else if (PropKit.getBoolean("isLocal", false)) {
			inv.invoke();
		}
		else {
			String token = controller.getHeader("token");

			if (TokenKit.verify(token)) {
				inv.invoke();
			}
			else {
				controller.getResponse().setStatus(403);
				controller.renderError(403);
				return;
			}
		}

	}

}
