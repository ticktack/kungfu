package org.kungfu.util;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KungfuKit {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    public static String lineToHump(String str) {
        Matcher matcher = linePattern.matcher(str.toLowerCase());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean hasUpperCase(String str) {

        for(int i=0; i<str.length(); i++) {

            char c = str.charAt(i);

            if (Character.isUpperCase(c)) {
                return true;
            }

        }

        return false;
    }

    public static <T> T toModel(Map<String, Object> map, Class<T> clazz) {
        return map == null ? null : JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    // 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String && (obj.equals(""))) {
            return true;
        } else if (obj instanceof Short && (Short) obj == 0) {
            return true;
        } else if (obj instanceof Integer && (Integer) obj == 0) {
            return true;
        } else if (obj instanceof Double && ((Double) obj) == 0) {
            return true;
        } else if (obj instanceof Float && ((Float) obj) == 0) {
            return true;
        } else if (obj instanceof Long && ((Long) obj) == 0) {
            return true;
        } else if (obj instanceof Boolean && !((Boolean) obj)) {
            return true;
        } else if (obj instanceof Collection && ((Collection<?>) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Map && ((Map<?, ?>) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;
        }
        return false;
    }

    public static Map<String, Object> toHump(String json) {
        try {
            Map<String, Object> map = (Map<String, Object>) JSON.parse(json);
            Map<String, Object> newmap = new HashMap<>();

            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.contains("_")) {
                    // key line to hump style
                    newmap.put(lineToHump(key), map.get(key));
                }
                else {
                    newmap.put(key, map.get(key));
                }
            }

            return newmap;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<Record> toHumpsList(List<Record> recordList) {
        List<Record> dist = new ArrayList<>();
        for (Record record : recordList) {
            Record r = new Record();
            r.setColumns(toHump(record.toJson()));
            dist.add(r);
        }

        return dist;
    }

    public static List<Map<String, Object>> toHumps(List<Record> recordList) {
        List<Map<String, Object>> dist = new ArrayList<>();
        for (Record record : recordList) {
            dist.add(toHump(record.toJson()));
        }

        return dist;
    }

    public static <M extends Model<M>> List<M> toHumpModelList(List<M> list) {
        List<M> resultList = list.stream()
                .map(record -> {
                    M r = record;
                    String[] attrs = r._getAttrNames();
                    r._setOrPut(toHump(record.toJson()));
                    r.remove(attrs);
                    return r;
                }).collect(Collectors.toList());

        return resultList;
    }

    public static <T> T toModel(String json, Class<T> clazz) {
        return StrKit.isBlank(json) ? null : JSON.parseObject(json, clazz);
    }

    public static Map<String, Object> json2Map(String json) {
        try {
            Map<String, Object> map = (Map<String, Object>) JSON.parse(json);
            Map<String, Object> result = new HashMap<>();

            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (KungfuKit.hasUpperCase(key)) {
                    // key hump to line style
                    result.put(KungfuKit.humpToLine(key), map.get(key));
                }
                else {
                    result.put(key, map.get(key));
                }
            }

            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Model> T toModelValidator(String json, Class<T> clazz) throws Exception {
        if (StrKit.isBlank(json)) {
            return null;
        }
        T entity = clazz.newInstance();
        entity._setAttrs(json2Map(json));
        return entity;
    }

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String path = packageName.replace('.', '/');
        java.net.URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("Package " + packageName + " not found on classpath.");
        }
        java.io.File directory = new java.io.File(resource.getFile());
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        Class<?> cls = Class.forName(packageName + '.' + file.substring(0, file.length() - 6));
                        classes.add(cls);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new RuntimeException("Package " + packageName + " not found on filesystem.");
        }
        return classes;
    }
    public static Map<String, List<String>> apiMethods(String packageName) {
        Map<String, List<String>> methodsMap = new HashMap<>();
        List<String> methodsList;
        List<Class<?>> classes = getClasses(packageName);
        for (Class<?> clazz : classes) {
            Method[] methods = clazz.getDeclaredMethods();
            methodsList = new ArrayList<>();
            for (Method method : methods) {
                if (method.getModifiers() == java.lang.reflect.Modifier.PUBLIC) {
                    methodsList.add(method.getName());
                }
            }
            methodsMap.put(StrKit.firstCharToLowerCase(clazz.getSimpleName().replace("Controller", "")), methodsList);
        }
        return methodsMap;
    }
}
