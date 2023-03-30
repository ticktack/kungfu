package org.kungfu.generator;

public class KungfuSharedMethods {
    public static String toJavaType(String columnType) {
        switch (columnType.toUpperCase()) {
            case "INT":
            case "INTEGER":
            case "TINYINT":
                return "Integer";
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

    public static String toJavaMethod(String columnType) {
        switch (columnType.toUpperCase()) {
            case "INT":
            case "INTEGER":
            case "TINYINT":
                return "Int";
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

}