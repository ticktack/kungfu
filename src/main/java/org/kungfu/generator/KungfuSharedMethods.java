package org.kungfu.generator;

import com.jfinal.kit.StrKit;

public class KungfuSharedMethods {
    public static String toJavaType(String dataTye, String columnType) {
        switch (dataTye.toUpperCase()) {
            case "INT":
            case "INTEGER":
                return "Integer";
            case "TINYINT":
                if (columnType.equals("tinyint(1)")) {
                    return "Boolean";
                }
                else {
                    return "Integer";
                }
            case "BIGINT":
                return "Long";
            case "FLOAT":
                return "Float";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
                return "BigDecimal";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
                return "String";
//            case "DATE":
//                return "LocalDate";
//            case "TIME":
//                return "LocalTime";
//            case "DATETIME":
//            case "TIMESTAMP":
//                return "LocalDateTime";
            case "DATE":
            case "TIME":
            case "DATETIME":
            case "TIMESTAMP":
                return "Date";
            case "BOOLEAN":
                return "Boolean";
            default:
                throw new IllegalArgumentException("Unsupported column type: " + columnType);
        }
    }

    public static String toJavaMethod(String dataTye, String columnType) {
        switch (dataTye.toUpperCase()) {
            case "INT":
            case "INTEGER":
                return "Int";
            case "TINYINT":
                if (columnType.equals("tinyint(1)")) {
                    return "";
                }
                else {
                    return "Int";
                }
            case "BIGINT":
                return "Long";
            case "FLOAT":
                return "Float";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
                return "BigDecimal";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
                return "Str";
            case "DATE":
            case "TIME":
            case "DATETIME":
            case "TIMESTAMP":
                return "Date";
            case "BOOLEAN":
                return "Boolean";
            default:
                throw new IllegalArgumentException("Unsupported column type: " + columnType);
        }
    }

    public static String toModelType(String columnType) {
        switch (columnType.toUpperCase()) {
            case "INT":
            case "INTEGER":
            case "TINYINT":
                return "getInt";
            case "BIGINT":
                return "getLong";
            case "FLOAT":
                return "Float";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
                return "BigDecimal";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
                return "String";
            case "DATE":
                return "LocalDate";
            case "TIME":
                return "LocalTime";
            case "DATETIME":
            case "TIMESTAMP":
                return "LocalDateTime";
            case "BOOLEAN":
                return "Boolean";
            default:
                throw new IllegalArgumentException("Unsupported column type: " + columnType);
        }
    }

    public static String toExample(String columnType) {
        switch (columnType.toUpperCase()) {
            case "INT":
            case "INTEGER":
            case "TINYINT":
                return "1";
            case "BIGINT":
                return "100";
            case "FLOAT":
                return "10.8";
            case "DOUBLE":
                return "3.56";
            case "DECIMAL":
                return "3000";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
                return "示例值";
            case "DATE":
                return "2023-03-01";
            case "TIME":
                return "15:30:22";
            case "DATETIME":
            case "TIMESTAMP":
                return "2023-03-01 15:30:22";
            case "BOOLEAN":
                return "true";
            default:
                throw new IllegalArgumentException("Unsupported column type: " + columnType);
        }
    }

    public static String getTreeCode(String tableName) {
        // sysMenu.getMenuCode()
        return String.format("%s.get%sCode()", StrKit.toCamelCase(tableName), StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName.substring(tableName.indexOf("_")))));
    }

    public static String getTreeName(String tableName) {
        // getMenuName()
        return String.format("get%sName()", StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName.substring(tableName.indexOf("_")))));
    }

    public static String getCodeColumn(String tableName) {
        // menu_code
        return String.format("%s_code", StrKit.toCamelCase(tableName.substring(tableName.indexOf("_"))));
    }

    public static String getCodeColumnName(String tableName) {
        // menu_name
        return String.format("%s_name", StrKit.toCamelCase(tableName.substring(tableName.indexOf("_"))));
    }



    public static void main(String[] args) {
        System.out.println(getCodeColumn("sys_menu"));
    }
}