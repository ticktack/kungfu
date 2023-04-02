package org.kungfu.validator;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import org.kungfu.core.KungfuConstant;

public class HeaderValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        setRet(Ret.fail());

        String userName = c.getHeader("userName");
        String userIdStr = c.getHeader("userId");
        if (StrKit.isBlank(userName) || StrKit.isBlank(userIdStr)) {
            addError(KungfuConstant.MASSAGE, "Header 请求中需要设置userId和userName");
        }

    }

    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
    }
}
