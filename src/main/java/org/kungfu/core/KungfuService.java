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

    public String buildQuerySql(QueryCondition qc, Model<M> model, Class<? extends Model<M>> clazz, String buildQueryType) {
        Table table = getTable(clazz);
        String selectSql = String.format("select * from %s where 1=1", table.getName());
        String orderBySql = "";
        if (!KungfuConstant.QUERY_TYPE_ONE.equals(buildQueryType)) {
            if (!qc.getOrderColumnName().contains(",")) {
                orderBySql = String.format(" order by %s %s", KungfuKit.humpToLine(qc.getOrderColumnName()), StrKit.isBlank(qc.getOrderBy()) ? "asc" : qc.getOrderBy());
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append(" order by");
                String[] orderByColumns = qc.getOrderColumnName().split(",");
                String[] orderByTypes = qc.getOrderBy().split(",");
                for (int i = 0; i < orderByColumns.length; i++) {
                    sb.append(String.format(" %s %s,", KungfuKit.humpToLine(orderByColumns[i]), orderByTypes[i]));
                }
                sb.deleteCharAt(sb.toString().length() - 1);

                orderBySql = sb.toString();
            }
        }

        if (model == null) {
            return selectSql + orderBySql;
        }

        StringBuilder whereSql = new StringBuilder();
        String[] columns = model._getAttrNames();
        for (String column : columns) {
            String key = KungfuKit.lineToHump(column);
            QueryTypeEnum queryTypeMenu = QueryTypeEnum.EQUAL;
            if (qc.getQueryTypeMap() != null) {
                queryTypeMenu = QueryTypeEnum.getByCode(qc.getQueryTypeMap().get(key));
                if (queryTypeMenu == null) {
                    queryTypeMenu = QueryTypeEnum.EQUAL;
                }
            }

            switch (queryTypeMenu) {
                case EQUAL:
                    if (table.getColumnNameSet().contains("parent_code") && column.contains("_code")) {
                        //whereSql.append(String.format(" and (%s = #para(%s) or parent_code = #para(%s))", column, key, key));
                        whereSql.append(String.format(" and parent_code = #para(%s)", key));
                    }
                    else {
                        whereSql.append(String.format(" and %s = #para(%s)", column, key));
                    }
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
                case NOT_EQUAL:
                    whereSql.append(String.format(" and %s <> #para(%s)", column, key));
                    break;
                case GREATER_EQUAL:
                    whereSql.append(String.format(" and %s >= #para(%s)", column, key));
                    break;
                case GREATER_THAN:
                    whereSql.append(String.format(" and %s > #para(%s)", column, key));
                case LESS_EQUAL:
                    whereSql.append(String.format(" and %s <= #para(%s)", column, key));
                    break;
                case LESS_THAN:
                    whereSql.append(String.format(" and %s < #para(%s)", column, key));
                // TODO 其他条件的查询语句生成渲染实现
            }

        }

        return selectSql + whereSql.toString() + orderBySql;
    }

    private DbTemplate query(QueryCondition qc, Class<? extends Model<M>> clazz, String buildQueryType) {

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

    public Page<Record> queryPage(QueryCondition qc, Class<? extends Model<M>> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_PAGE);

        return dbTemplate.paginate(qc.getPageNumber(), qc.getPageSize());
    }

    public List<Record> queryList(QueryCondition qc, Class<? extends Model<M>> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_LIST);

        return dbTemplate.find();
    }

    public Record queryOne(QueryCondition qc, Class<? extends Model<M>> clazz) {

        DbTemplate dbTemplate = query(qc, clazz, KungfuConstant.QUERY_TYPE_ONE);

        return dbTemplate.findFirst();
    }


    private List<Record> getChildListById(List<Record> list, Record t) {
        List<Record> childList = new ArrayList<>();
        for (Record next : list) {
            if (StrKit.notNull(next.getLong("pid")) && next.getLong("pid").longValue() == t.getLong("id").longValue()) {
                childList.add(next);
            }
        }
        return childList;
    }

    private List<Record> getChildListByCode(List<Record> list, Record t, String codeName) {
        List<Record> childList = new ArrayList<>();
        for (Record next : list) {
            if (StrKit.notNull(next.getStr("parentCode")) && next.getStr("parentCode").equals(t.getStr(codeName))) {
                childList.add(next);
            }
        }
        return childList;
    }

    private boolean hasChild(List<Record> list, Record t, String useIdOrCode) {
        return useIdOrCode == null ? getChildListById(list, t).size() > 0 : getChildListByCode(list, t, useIdOrCode).size() > 0;
    }

    private void recursion(List<Record> list, Record t, String useIdOrCode) {
        // 得到子节点列表
        List<Record> childList = useIdOrCode == null ? getChildListById(list, t) : getChildListByCode(list, t, useIdOrCode);

        t.set("children", childList);

        for (Record treeChild : childList) {

            if (hasChild(list, treeChild, useIdOrCode)) {
                // 判断是否有子节点
                for (Record next : childList) {
                    recursion(list, next, useIdOrCode);
                }
            }
        }
    }

    // 构建树结构方法
    public Record buildTree(List<Record> treeSourceList, String useIdOrCode, String nameColumn, String rootName) {
        return useIdOrCode.equals("id") ? buildTreeById(treeSourceList, nameColumn, rootName) : buildTreeByCode(treeSourceList, useIdOrCode, nameColumn, rootName);
    }

    public Record buildTreeById(List<Record> treeSourceList, String nameColumn, String rootName) {
        List<Record> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (Record record : treeSourceList) {
            tempList.add(record.getLong("id"));
        }
        for (Record record : treeSourceList) {
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
        root.set("pid", 0L);
        root.set(nameColumn, rootName);
        root.set("children", returnList);

        return root;
    }

    public Record buildTreeByCode(List<Record> treeSourceList, String codeName, String nameColumn, String rootName) {
        List<Record> returnList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();

        for (Record record : treeSourceList) {
            tempList.add(record.getStr(codeName));
        }

        for (Record record : treeSourceList) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(record.getStr("parentCode"))) {
                recursion(treeSourceList, record, codeName);
                returnList.add(record);
            }
        }

        if (returnList.isEmpty()) {
            returnList = treeSourceList;
        }

        Record root = new Record();
        root.set("id", 0L);
        root.set("parentCode", null);
        root.set("parentName", null);
        root.set(codeName, "root");
        root.set(nameColumn, rootName);
        root.set("children", returnList);

        return root;
    }

    public  R existValid(boolean isSave, Model<M> model, String codeAttr, String nameAttr) {
        Table table = getTable(model.getClass());
        String sql = String.format("select * from %s where %s=? or %s=?", table.getName(), codeAttr, nameAttr);

        if (isSave) {
            Record exist = Db.findFirst(sql, model.getStr(codeAttr), model.getStr(codeAttr));
            if (exist != null) {
                return R.fail(631, String.format("编码为'%s'已存在，请重新输入", model.getStr(codeAttr)));
            }

            exist = Db.findFirst(sql, model.getStr(nameAttr), model.getStr(nameAttr));
            if (exist != null) {
                return R.fail(632, String.format("名称为'%s'已存在，请重新输入", model.getStr(nameAttr)));
            }
        }

        return R.ok();
    }

    public  R existValid(boolean isSave, Model<M> model, String codeAttr) {
        Table table = getTable(model.getClass());
        String sql = String.format("select * from %s where %s=?", table.getName(), codeAttr);

        if (isSave) {
            Record exist = Db.findFirst(sql, model.getStr(codeAttr));
            if (exist != null) {
                return R.fail(631, String.format("编码为'%s'已存在，请重新输入", model.getStr(codeAttr)));
            }
        }

        return R.ok();
    }

}