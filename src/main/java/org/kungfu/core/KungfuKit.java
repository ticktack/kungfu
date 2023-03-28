package org.kungfu.core;

import com.alibaba.fastjson.JSON;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        } else if (obj instanceof Short && ((Short) obj).shortValue() == 0) {
            return true;
        } else if (obj instanceof Integer && ((Integer) obj).intValue() == 0) {
            return true;
        } else if (obj instanceof Double && ((Double) obj).doubleValue() == 0) {
            return true;
        } else if (obj instanceof Float && ((Float) obj).floatValue() == 0) {
            return true;
        } else if (obj instanceof Long && ((Long) obj).longValue() == 0) {
            return true;
        } else if (obj instanceof Boolean && !((Boolean) obj)) {
            return true;
        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;
        }
        return false;
    }
}
