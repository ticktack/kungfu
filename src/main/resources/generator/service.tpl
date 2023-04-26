package #(basePackage).modules.#(moduleName).service;
#set(className=firstCharToUpperCase(toCamelCase(tableName)))
#set(camelCaseName=toCamelCase(tableName))

import #(basePackage).modules.#(moduleName).model.#(className);
import com.jfinal.plugin.activerecord.Db;
import io.kungfu.panda.util.PinyinKit;
import org.kungfu.core.KungfuService;
import org.kungfu.core.R;
import org.kungfu.core.UserInfo;

import java.util.Date;

public class #(className)Service extends KungfuService<#(className)> {
    private final #(className) dao = #(className).dao;

    public R saveOrUpdate(#(className) #(camelCaseName), UserInfo userInfo) {

        if (#(camelCaseName) == null) {
            return R.fail(630, "信息不能为空");
        }

        Date date = new Date();
        if (#(toCamelCase(tableName)).getId() != null) {
            #(camelCaseName).setUpdateUser(userInfo.getUserName());
            #(camelCaseName).setUpdateUserId(userInfo.getUserId());
            #(camelCaseName).setUpdateTime(date);
            if (#(camelCaseName).update()) {
                //CacheKit.remove("#(camelCaseName)", "#(camelCaseName)Tree");
                return R.ok("更新成功");
            }

            return R.fail(641, "更新失败");
        }
        else {
            #(camelCaseName).setCreateUser(userInfo.getUserName());
            #(camelCaseName).setCreateUserId(userInfo.getUserId());
            #(camelCaseName).setCreateTime(date);

            if (#(camelCaseName).save()) {
                //CacheKit.removeAll("#(camelCaseName)");
                return R.ok("保存成功");
            }

            return R.fail(640, "保存失败");
        }

    }
}
