package org.kungfu.core;

public enum QueryTypeEnum {
    EQUAL("eq"),                // 相等
    NOT_EQUAL("neq"),           // 不相等
    LESS_THAN("lt"),            // 小于
    LESS_EQUAL("le"),           // 小于等于
    GREATER_EQUAL("ge"),        // 大于等于
    GREATER_THAN("gt"),         // 大于
    LIKE("like"),               // 模糊匹配 %xxx%
    LIKE_LEFT("like_left"),     // 左模糊 %xxx
    LIKE_RIGHT("like_right"),   // 右模糊 xxx%
    NOT_EMPTY("not_empty"),     // 不为空值的情况
    EMPTY("empty"),             // 空值的情况
    BETWEEN_AND("between_and"), // 区间
    IN("in"),                   // 在范围内
    NOT_IN("not_in");           // 不在范围内

    private String code;


    public static QueryTypeEnum getByCode(String code) {
        for (QueryTypeEnum e: QueryTypeEnum.values()) {
            if (code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    QueryTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
