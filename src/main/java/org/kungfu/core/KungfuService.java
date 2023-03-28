package org.kungfu.core;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected Table getTable(Model<M> model) {
        return TableMapping.me().getTable(model.getClass());
    }

    public String buildQuerySql(QueryCondition qc, Model<M> model, String buildQueryType) {
        Table table = getTable(model);
        String selectSql = String.format("select * from %s where 1=1", table.getName());
        String orderBySql = KungfuConstant.QUERY_TYPE_ONE.equals(buildQueryType) ? "" :
                String.format(" order by %s %s", qc.getOrderColumnName(), qc.getOrderBy());

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
        Model<M> model = KungfuKit.toModel(qc.getModelMap(), clazz);
        String pageSql = buildQuerySql(qc, model, buildQueryType);

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

}