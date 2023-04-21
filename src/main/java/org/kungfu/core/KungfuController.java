package org.kungfu.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import org.kungfu.util.KungfuKit;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KungfuController extends Controller {

    @Inject
    private KungfuService service;

    private Map<String, Object> toHump(String json) {
        Map<String, Object> map = JSON.parseObject(json, Map.class);
        Map<String, Object> result = new HashMap<>();

        for (String key : map.keySet()) {
            if (key.contains("_")) {
                // key line to hump style
                result.put(KungfuKit.lineToHump(key), map.get(key));
            } else {
                result.put(key, map.get(key));
            }
        }

        return result;
    }

    public Map<String, Object> toHump(Model model) {
        return toHump(model.toJson());
    }

    public Map<String, Object> toHump(Record record) {
        return toHump(record.toJson());
    }

    public Record toHumpRecord(Record record) {
        Record r = new Record();
        r.setColumns(toHump(record));
        return r;
    }
   
    public Page<Record> toHumpRecordPage(Page<Record> page) {
        List<Record> resultList = page.getList().stream()
                .map(record -> {
                    Record r = new Record();
                    r.setColumns(toHump(record));
                    return r;
                })
                .collect(Collectors.toList());

        page.setList(resultList);

        return page;
    }
   
    public <M extends Model<M>> Page<M> toHumpModelPage(Page<M> page) {
        List<M> resultList = page.getList().stream()
                .map(record -> {
                    M r = record;
                    String[] attrs = r._getAttrNames();
                    r._setOrPut(toHump(record));
                    r.remove(attrs);
                    return r;
                }).collect(Collectors.toList());

        page.setList(resultList);
        return page;
    }
   
    public <M extends Model<M>> List<Map<String, Object>> toHumpModelList(List<M> recordList) {
        List<Map<String, Object>> dist = new ArrayList<>();
        for (M record : recordList) {
            dist.add(toHump(record.toJson()));
        }

        return dist;
    }

    public List<Map<String, Object>> toHumpRecordList(List<Record> recordList) {
        List<Map<String, Object>> dist = new ArrayList<>();
        for (Record record : recordList) {
            dist.add(toHump(record.toJson()));
        }

        return dist;
    }
   
    public UserInfo getUserInfo() {
        String userName = getHeader("userName");
        String userIdStr = getHeader("userId");
        long userId = 0L;
        if (StrKit.notBlank(userName)) {
            try {
                userName = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        if (StrKit.notBlank(userIdStr)) {
            userId = Long.parseLong(userIdStr);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setUserId(userId);

        return userInfo;
    }

    public String getRequestBody() {
        return getAttr(KungfuConstant.JSON_REQUEST_BODY);
    }
   
    public String toStr(String key) {
        return toParam(key, String::toString);
    }

    public String toStr(String key, String defaultValue) {
        String result = toParam(key, String::toString);
        return result == null ? defaultValue : result;
    }

    public Integer toInt(String key) {
        return toParam(key, Integer::parseInt);
    }
    public Integer toInt(String key, int defaultValue) {
        Integer result = toParam(key, Integer::parseInt);
        return result == null ? defaultValue : result;
    }
   
    public Long toLong(String key) {
        return toParam(key, Long::parseLong);
    }

    public Double toDouble(String key) {
        return toParam(key, Double::parseDouble);
    }

    public Boolean toBoolean(String key) {
        return toParam(key, Boolean::parseBoolean);
    }

    public Date toDate(String key) {
        return StrKit.isBlank(getRequestBody()) ? null : JSON.parseObject(getRequestBody()).getDate(key);
    }

    public Timestamp toTimestamp(String key) {
        return StrKit.isBlank(getRequestBody()) ? null : JSON.parseObject(getRequestBody()).getTimestamp(key);
    }

    public <T> T toParam(String key, Function<String, T> mapper) {
        String result = JSON.parseObject(getRequestBody()).getString(key);
        if (StrKit.isBlank(result)) {
            return null;
        }
        return StrKit.isBlank(getRequestBody()) ? null : mapper.apply(result);
    }

    public <T> T toModel(String key, Class<T> clazz) {
        return StrKit.isBlank(getRequestBody()) ? null : JSON.parseObject(getRequestBody()).getObject(key, clazz);
    }

    public <T> T toModel(String jsonRequest, String key, Class<T> clazz) {
        return StrKit.isBlank(jsonRequest) ? null : JSON.parseObject(jsonRequest).getObject(key, clazz);
    }

    public <T> T toModel(Map<String, Object> map, Class<T> clazz) {
        return map == null ? null : JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    public Map<String, Object> toMap() {
        return StrKit.isBlank(getRequestBody()) ? null : KungfuKit.json2Map(getRequestBody());
    }
    public Map<String, Object> toMapByKey(String key) {
        return StrKit.isBlank(getRequestBody()) ? null : KungfuKit.json2Map(JSON.parseObject(getRequestBody()).getString(key));
    }


    public <T> T toDTO(String key, Class<T> clazz) {
        return StrKit.isBlank(getRequestBody()) ? null : JSON.parseObject(getRequestBody()).getObject(key, clazz);
    }

    public <T> T toDTO(Class<T> clazz) {
        return StrKit.isBlank(getRequestBody()) ? null : JSON.parseObject(getRequestBody(),clazz);
    }

    public <T extends Model> T toModel(Class<T> clazz) {
        try {
            T entity = clazz.newInstance();
            entity._setAttrs(KungfuKit.json2Map(getRequestBody()));
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> toModelList(String key, Class<T> clazz) {
        return StrKit.isBlank(getRequestBody()) ? null : JSONArray.parseArray(JSON.parseObject(getRequestBody()).getString(key), clazz);
    }

    public <T> List<T> toModelList(Class<T> clazz) {
        return StrKit.isBlank(getRequestBody()) ? null : JSONArray.parseArray(getRequestBody(), clazz);
    }

    public static  <T extends Number> T[] toArray(String splitString, Function<String, T> mapper) {
        String[] stringArray = splitString.split(",");
        T[] array = (T[]) Array.newInstance(mapper.apply(stringArray[0]).getClass(), stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            array[i] = mapper.apply(stringArray[i]);
        }
        return array;
    }

    public QueryCondition wapperQueryCondition(QueryCondition qc, String queryType) {
        if (qc == null) {
            qc = new QueryCondition();
        }

        if (KungfuConstant.QUERY_TYPE_PAGE.equals(queryType)) {
            if (KungfuKit.isNullOrEmpty(qc.getPageNumber())) {
                qc.setPageNumber(KungfuConstant.DEFAULT_PAGE_NUMBER);
            }

            if (KungfuKit.isNullOrEmpty(qc.getPageSize())) {
                qc.setPageSize(KungfuConstant.DEFAULT_PAGE_SIZE);
            }
        }
        if (KungfuConstant.QUERY_TYPE_PAGE.equals(queryType) || KungfuConstant.QUERY_TYPE_LIST.equals(queryType) ) {
            if (KungfuKit.isNullOrEmpty(qc.getOrderColumnName())) {
                qc.setOrderColumnName(KungfuConstant.DEFAULT_ORDER_COLUMN);
            }

            if (KungfuKit.isNullOrEmpty(qc.getOrderBy())) {
                qc.setOrderBy(KungfuConstant.DEFAULT_ORDER_BY);
            }
        }

        return qc;
    }


    public Page<Record> queryPage(Class<? extends Model> clazz) {

        QueryCondition qc = toDTO(QueryCondition.class);

        qc = wapperQueryCondition(qc, KungfuConstant.QUERY_TYPE_PAGE);

        Page<Record> page = service.queryPage(qc, clazz);

        return page == null ? null : toHumpRecordPage(page);
    }


    protected Table getTable(Class<? extends Model> clazz) {
        return TableMapping.me().getTable(clazz);
    }

    public R selectById(Long recordId, Class<? extends Model> clazz) {

        if (recordId == null) {
           return R.fail("查询记录ID为空");
        }
        Table table = getTable(clazz);

        Record record = Db.findById(table.getName(), recordId);

        return R.ok("data", record == null ? null : toHumpRecord(record));
    }

    public R deleteById(Long recordId, Class<? extends Model> clazz) {

        if (recordId == null) {
            return R.fail(661, "删除记录ID为空");
        }

        Table table = getTable(clazz);

        boolean result = Db.deleteById(table.getName(), recordId);

        return result ? R.ok("删除成功") : R.fail("删除失败");
    }

    public R deleteByIds(String ids , Class<? extends Model> clazz) {
        if (StrKit.isBlank(ids)) {
            return R.fail(661, "删除记录ids为空");
        }
        // 事务控制
        boolean flag = Db.tx(() -> {
            Long[] idsArray = toArray(ids, Long::new);
            Table table = getTable(clazz);
            for (Long id : idsArray) {
                if (!Db.deleteById(table.getName(), id)) {
                    return false;
                }
            }
            return true;
        });

        return flag ? R.ok("删除成功") : R.fail("删除失败");
    }

    public <T> void paramValid(T param, String paramName) {
        if (KungfuKit.isNullOrEmpty(param)) {
            renderJson(R.fail(640, String.format("%s参数为空", paramName)));
            return;
        }
    }

    public <T> void paramValid(T param, int errorCode, String paramName) {
        if (KungfuKit.isNullOrEmpty(param)) {
            renderJson(R.fail(errorCode, String.format("%s参数为空", paramName)));
            return;
        }
    }
}