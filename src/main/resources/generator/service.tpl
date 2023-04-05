package #(basePackage).modules.#(moduleName).service;
#set(className=firstCharToUpperCase(toCamelCase(tableName)))
#set(camelCaseName=toCamelCase(tableName))

import #(basePackage).modules.#(moduleName).model.#(className);
import org.kungfu.core.*;
import org.kungfu.core.R;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import java.util.Date;

public class #(className)Service extends KungfuService<#(className)> {
    private #(className) dao = #(className).dao;

    public R saveOrUpdate(#(className) #(camelCaseName), UserInfo userInfo) {

        if (#(camelCaseName) == null) {
            return R.fail(610, "信息不能为空");
        }

        Date date = new Date();
        if (#(toCamelCase(tableName)).getId() != null) {
            #(camelCaseName).setUpdateUser(userInfo.getUserName());
            #(camelCaseName).setUpdateUserId(userInfo.getUserId());
            #(camelCaseName).setUpdateTime(date);
            if (#(camelCaseName).update()) {
                return R.ok("更新成功");
            }

            return R.fail(620, "更新失败");
        }
        else {
            #(camelCaseName).setCreateUser(userInfo.getUserName());
            #(camelCaseName).setCreateUserId(userInfo.getUserId());
            #(camelCaseName).setCreateTime(date);

            if (#(camelCaseName).save()) {
                return R.ok("保存成功");
            }

            return R.fail(630, "保存失败");
        }

    }

    public Record selectById(Long #(toCamelCase(tableName))Id) {

        return Db.findById("#(tableName)", #(toCamelCase(tableName))Id);
    }

    public R deleteByIds(Long[] deleteByIds) {
        for (Long #(camelCaseName)Id : deleteByIds) {
            if (!dao.deleteById(#(camelCaseName)Id)) {
                return R.fail("删除失败");
            }
        }

        return R.ok("删除成功");
    }

    public Page<Record> page(Integer pageNumber, Integer pageSize) {
        String fromSql = "from #(tableName) order by create_time desc";

        return Db.paginate(pageNumber, pageSize, "select *", fromSql);
    }

    public Page<Record> page(QueryCondition qc, Class<? extends Model> clazz) {
        return queryPage(qc, clazz);
    }
}
