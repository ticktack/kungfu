package #(basePackage).modules.#(moduleName).service;

import #(basePackage).modules.#(moduleName).model.#(firstCharToUpperCase(toCamelCase(tableName)));
import org.kungfu.core.KungfuService;
import org.kungfu.core.QueryCondition;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;

public class #(firstCharToUpperCase(toCamelCase(tableName)))Service extends KungfuService<CodeTable> {
    public Ret saveOrUpdate(#(firstCharToUpperCase(toCamelCase(tableName))) #(toCamelCase(tableName)), String userCode) {

        if (#(toCamelCase(tableName)) == null) {
            return Ret.fail("信息不能为空");
        }

        Date date = new Date();
        #(toCamelCase(tableName)).setUpdateUser(userCode);
        #(toCamelCase(tableName)).setUpdateTime(date);
        if (#(toCamelCase(tableName)).getId() != null) {

            if (#(toCamelCase(tableName)).update()) {
                return Ret.ok("更新成功");
            }

            return Ret.ok("更新失败");
        }
        else {
            #(toCamelCase(tableName)).setCreateUser(userCode);
            #(toCamelCase(tableName)).setCreateTime(date);

            if (#(toCamelCase(tableName)).save()) {
                return Ret.ok("保存成功");
            }

            return Ret.fail("保存失败");
        }

    }

    public Record selectById(Long #(toCamelCase(tableName))Id) {

        return Db.findById("#(tableName)", #(toCamelCase(tableName))Id);
    }
    public Ret deleteById(Long #(toCamelCase(tableName))Id) {

        if(Db.deleteById("#(tableName)", #(toCamelCase(tableName))Id)) {
            return Ret.ok("删除成功");
        }

        return Ret.fail("删除失败");
    }

    public Page<Record> page(Integer pageNumber, Integer pageSize) {
        String fromSql = "from #(tableName) order by create_time desc";

        return Db.paginate(pageNumber, pageSize, "select *", fromSql);
    }

    public Page<Record> page(QueryCondition qc, Class<? extends Model> clazz) {
        return queryPage(qc, clazz);
    }
}
