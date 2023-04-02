package org.kungfu.validator;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import org.kungfu.core.KungfuConstant;

public class PostRequestValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        setRet(Ret.fail());
        String jsonString = c.getAttr(KungfuConstant.JSON_REQUEST_BODY);
        String method = c.getRequest().getMethod();
        if (method.equals(KungfuConstant.HTTP_METHOD_POST) && StrKit.isBlank(jsonString)) {
            addError(KungfuConstant.MASSAGE, "POST 请求参数不能为空");
        }

    }

    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
    }
}
