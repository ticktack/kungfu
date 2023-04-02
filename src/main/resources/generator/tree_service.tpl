package #(basePackage).modules.#(moduleName).service;

import #(basePackage).modules.#(moduleName).model.#(firstCharToUpperCase(toCamelCase(tableName)));
import org.kungfu.core.*;
import org.kungfu.util.KungfuKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import java.util.Date;
import java.util.List;

#set(className=firstCharToUpperCase(toCamelCase(tableName)))
#set(camelCaseName=toCamelCase(tableName))
#set(treeCode=getTreeCode(tableName))
#set(treeName=getTreeName(tableName))
#set(codeColumn=getCodeColumn(tableName))
#set(codeColumnName=getCodeColumnName(tableName))
#set(tableComment=tableComment.replace("表",""))
public class #(className)Service extends KungfuService<#(className)> {
    private #(className) dao = #(className).dao;

    public Record buildTree(String queryName) {
        String sql = "select * from #(tableName) order by display_no";

        if (StrKit.notBlank(queryName)) {
            sql = "select * from #(tableName) where #(codeColumnName) like '%" + queryName + "%' order by display_no";
        }

        List<Record> #(camelCaseName)List = Db.find(sql);

        return buildTree(KungfuKit.toHumpsList(#(camelCaseName)List), "#(codeColumn)", "#(tableComment)");
    }

    public #(className) findByCode(String code) {
        return dao.findFirst("select * from #(tableName) where #(codeColumn)=?", code);
    }

    public Ret saveOrUpdate(#(className) #(camelCaseName), UserInfo userInfo) {

        if (#(camelCaseName) == null) {
            return Ret.fail("信息不能为空");
        }

        if (#(camelCaseName).getId() == null) {
            #(className) exist = findByCode(#(treeCode));
            if (exist != null) {
                return Ret.fail("编码已存在，请重新输入");
            }
        }

        #(className) parent = findByCode(#(camelCaseName).getParentCode());
        if (parent == null) {
            #(camelCaseName).setParentName("#(tableComment)");
        }
        else {
            #(camelCaseName).setParentName(parent.#(treeName));
        }

        Date date = new Date();

        if (#(toCamelCase(tableName)).getId() != null) {
            #(camelCaseName).setUpdateUser(userInfo.getUserName());
            #(camelCaseName).setUpdateUserId(userInfo.getUserId());
            #(camelCaseName).setUpdateTime(date);
            if (#(camelCaseName).update()) {
                return Ret.ok("更新成功");
            }

            return Ret.ok("更新失败");
        }
        else {
            #(camelCaseName).setCreateUser(userInfo.getUserName());
            #(camelCaseName).setCreateUserId(userInfo.getUserId());
            #(camelCaseName).setCreateTime(date);

            if (#(camelCaseName).save()) {
                return Ret.ok("保存成功");
            }

            return Ret.fail("保存失败");
        }

    }

    public Record selectById(Long #(toCamelCase(tableName))Id) {

        return Db.findById("#(tableName)", #(toCamelCase(tableName))Id);
    }

    public Ret deleteByIds(Long[] deleteByIds) {
        for (Long #(camelCaseName)Id : deleteByIds) {
            if (!dao.deleteById(#(camelCaseName)Id)) {
                return Ret.fail("删除失败");
            }
        }

        return Ret.ok("删除成功");
    }

    public Page<Record> page(Integer pageNumber, Integer pageSize) {
        String fromSql = "from #(tableName) order by create_time desc";

        return Db.paginate(pageNumber, pageSize, "select *", fromSql);
    }

    public Page<Record> page(QueryCondition qc, Class<? extends Model> clazz) {
        return queryPage(qc, clazz);
    }

    public boolean setStatus(Long #(camelCaseName)Id, Boolean isEnabled, UserInfo userInfo) {
        #(className) #(camelCaseName) = dao.findById(#(camelCaseName)Id);
        #(camelCaseName).setIsEnabled(isEnabled);
        #(camelCaseName).setUpdateUserId(userInfo.getUserId());
        #(camelCaseName).setUpdateUser(userInfo.getUserName());
        #(camelCaseName).setUpdateTime(new Date());
        return #(camelCaseName).update();
    }
}
