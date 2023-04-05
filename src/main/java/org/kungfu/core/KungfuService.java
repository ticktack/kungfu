package org.kungfu.core;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import org.kungfu.util.KungfuKit;

import java.util.*;

public class KungfuService <M extends Model<M>> {

    public static Map<String, Object> toMap(Model<?> model) {
        Map<String, Object> map = new HashMap<>();
        String[] names = model._getAttrNames();
        for (String str : names) {
            if (model.get(str) != null) {
                map.put(str, model.get(str));
            }
        }
        return map;
    }

    public static Map<String, Object> toMap(Record record) {
        Map<String, Object> map = new HashMap<>();
        if (record != null) {
            String[] columns = record.getColumnNames();
            for (String col : columns) {
                map.put(col, record.get(col));
            }
        }
        return map;
    }
    protected Table getTable(Class<? extends Model> clazz) {
        return TableMapping.me().getTable(clazz);
    }

    public String buildQuerySql(QueryCondition qc, Model<M> model, Class<? extends Model> clazz, String buildQueryType) {
        Table table = getTable(clazz);
        String selectSql = String.format("select * from %s where 1=1", table.getName());
        String orderBySql = KungfuConstant.QUERY_TYPE_ONE.equals(buildQueryType) ? "" :
                String.format(" order by %s %s", qc.getOrderColumnName(), qc.getOrderBy());

        if (model == null) {
            return selectSql + orderBySql;
        }

        StringBuffer whereSql = new StringBuffer();
        String[] columns = model._getAttrNames();
        for (String column : columns) {
            String key = KungfuKit.lineToHump(column);
            Object object = qc.getModelMap().get(key);
            if (!KungfuKit.isNullOrEmpty(object)) {
                QueryTypeEnum queryTypeMenu = QueryTypeEnum.getByCode(qc.getQueryTypeMap().get(key));
                switch (queryTypeMenu) {
                    case EQUAL:
                        whereSql.append(String.format(" and %s = #para(%s)", column, key));
                        break;
                    case LIKE:
                        whereSql.append(String.format(" and %s like concat('%%', #para(%s), '%%')", column, key));
                        break;
                    case LIKE_LEFT:
                        whereSql.append(String.format(" and %s like concat('%%', #para(%s))", column, key));
                        break;
                    case LIKE_RIGHT:
                        whereSql.append(String.format(" and %s like concat(#para(%s), '%%')", column, key));
                        break;
                    // TODO 其他条件的查询语句生成渲染实现
                }
            }

        }

        return selectSql + whereSql.toString() + orderBySql;
    }

    private DbTemplate query(QueryCondition qc, Class<? extends Model> clazz, String buildQueryType) {

        if (qc.getModelMap() == null || qc.getModelMap().isEmpty()) {
            String pageSql = buildQuerySql(qc, null, clazz, buildQueryType);
            return Db.templateByString(pageSql, Kv.create());
        }

        Model<M> model = KungfuKit.toModel(qc.getModelMap(), clazz);
        String pageSql = buildQuerySql(qc, model,clazz, buildQueryType);

        Kv params = Kv.create();
        params.set(qc.getModelMap());

        return Db.templateByString(pageSql, params);
    }

    public Page<Record> queryPage(QueryCondition qc, Class<? extends Model> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_PAGE);

        return dbTemplate.paginate(qc.getPageNumber(), qc.getPageSize());
    }

    public List<Record> queryList(QueryCondition qc, Class<? extends Model> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_LIST);

        return dbTemplate.find();
    }

    public Record queryOne(QueryCondition qc, Class<? extends Model> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_ONE);

        return dbTemplate.findFirst();
    }


    private List<Record> getChildListById(List<Record> list, Record t) {
        List<Record> tlist = new ArrayList<>();
        Iterator<Record> it = list.iterator();
        while (it.hasNext()) {
            Record next = it.next();
            if (StrKit.notNull(next.getLong("pid")) && next.getLong("pid").longValue() == t.getLong("id").longValue()) {
                tlist.add(next);
            }
        }
        return tlist;
    }

    private List<Record> getChildListByCode(List<Record> list, Record t, String codeName) {
        List<Record> tlist = new ArrayList<>();
        Iterator<Record> it = list.iterator();
        while (it.hasNext()) {
            Record next = it.next();
            if (StrKit.notNull(next.getStr("parent_code")) && next.getStr("parent_code").equals(t.getStr(codeName))) {
                tlist.add(next);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<Record> list, Record t, String useIdOrCode) {
        return (useIdOrCode == null ? getChildListById(list, t).size() > 0 : getChildListByCode(list, t, useIdOrCode).size() > 0) ? true : false;
    }

    private void recursion(List<Record> list, Record t, String useIdOrCode) {
        // 得到子节点列表
        List<Record> childList = useIdOrCode == null ? getChildListById(list, t) : getChildListByCode(list, t, useIdOrCode);

        t.set("children", childList);

        for (Record tChild : childList) {

            if (hasChild(list, tChild, useIdOrCode)) {
                // 判断是否有子节点
                Iterator<Record> it = childList.iterator();
                while (it.hasNext()) {
                    Record next = it.next();
                    recursion(list, next, useIdOrCode);
                }
            }
        }
    }

    // 构建树结构方法
    public Record buildTree(List<Record> treeSourceList, String useIdOrCode, String rootName) {
        return useIdOrCode.equals("id") ? buildTreeById(treeSourceList, rootName) : buildTreeByCode(treeSourceList, useIdOrCode, rootName);
    }

    public Record buildTreeById(List<Record> treeSourceList, String rootName) {
        List<Record> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (Record record : treeSourceList) {
            tempList.add(record.getLong("id"));
        }
        for (Iterator<Record> iterator = treeSourceList.iterator(); iterator.hasNext();) {
            Record record = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(record.getLong("pid"))) {
                recursion(treeSourceList, record, null);
                returnList.add(record);
            }
        }
        if (returnList.isEmpty()) {
            returnList = treeSourceList;
        }

        Record root = new Record();
        root.set("id", 0L);
        root.set("name", rootName);
        root.set("children", returnList);

        return root;
    }

    public Record buildTreeByCode(List<Record> treeSourceList, String codeName, String rootName) {
        List<Record> returnList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();

        for (Record record : treeSourceList) {
            tempList.add(record.getStr(codeName));
        }

        for (Iterator<Record> iterator = treeSourceList.iterator(); iterator.hasNext();) {
            Record record = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(record.getStr("parent_code"))) {
                recursion(treeSourceList, record, codeName);
                returnList.add(record);
            }
        }

        if (returnList.isEmpty()) {
            returnList = treeSourceList;
        }

        Record root = new Record();
        root.set("id", 0L);
        root.set(KungfuKit.lineToHump(codeName), "root");
        root.set("name", rootName);
        root.set("children", returnList);

        return root;
    }

}