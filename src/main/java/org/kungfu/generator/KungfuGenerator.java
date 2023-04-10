package org.kungfu.generator;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import org.kungfu.util.FileKit;

import java.io.File;
import java.util.List;
import java.util.Map;

public class KungfuGenerator {
    String GEN_MYSQL_URL = "jdbc:mysql://%s/%s?characterEncoding=utf8&useSSL=false&zeroDateTimeBehavior=convertToNull";
    private static Template template = null;
    private static Engine engine;
    protected String baseModelTemplate = "/generator/basemodel.tpl";
    protected String modelTemplate = "/generator/model.tpl";
    protected String dtoTemplate = "/generator/dto.tpl";
    protected String validateTemplate = "/generator/validate.tpl";
    protected String serviceTemplate = "/generator/service.tpl";
    protected String serviceTreeTemplate = "/generator/tree_service.tpl";
    protected String controllerTemplate = "/generator/controller.tpl";
    protected String controllerTreeTemplate = "/generator/tree_controller.tpl";
    protected final static String LAYERED_BASE_MODEL = "base";
    protected final static String LAYERED_MODEL = "model";
    protected final static String LAYERED_DTO = "dto";
    protected final static String LAYERED_VALIDATE = "validate";
    protected final static String LAYERED_SERVICE = "service";
    protected final static String LAYERED_TREE_SERVICE = "tree_service";
    protected final static String LAYERED_CONTROLLER = "controller";
    protected final static String LAYERED_TREE_CONTROLLER = "tree_controller";

    private final static String[] LAYERED_ARRAY = new String[]{LAYERED_BASE_MODEL, LAYERED_MODEL, LAYERED_DTO, LAYERED_VALIDATE, LAYERED_SERVICE, LAYERED_CONTROLLER};

    private final static String[] LAYERED_TREE_ARRAY = new String[]{LAYERED_BASE_MODEL, LAYERED_MODEL, LAYERED_DTO, LAYERED_VALIDATE, LAYERED_TREE_SERVICE, LAYERED_TREE_CONTROLLER};

    private final static String BASE_MODEL_SQL = "select column_name,data_type,column_type,is_nullable,column_default,column_comment from information_schema.`columns` where table_schema='%s' and table_name='%s'";
    private final static String DTO_SQL = BASE_MODEL_SQL + " and column_name not in('pinyin','create_user','create_user_id','create_by','create_dept','create_time','update_user','update_user_id','update_by','update_time','status','is_deleted')";

    private final static String IF_TREE_TABLE_SQL = BASE_MODEL_SQL + " and column_name in ('parent_code','pid')";

    private final static String TABLE_SQL = "select table_name, table_comment from information_schema.`tables` where table_schema='%s'";

    public void init(String databaseHost, String databaseName, String username, String password) {
        // 连接数据源
        DruidPlugin dp = new DruidPlugin(String.format(GEN_MYSQL_URL, databaseHost, databaseName), username, password);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        dp.start();
        arp.start();

        // 初始化模板引擎
        engine = Engine.use();
        engine.addSharedMethod(new StrKit());
        engine.addSharedMethod(new KungfuSharedMethods());
        engine.setDevMode(true);
        engine.setToClassPathSourceFactory();
    }

    private void mkdir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getTemplateByType(boolean isBlank, Map<String, String> templateMap, String templateType, String defaultTemplate) {
        if (isBlank || templateMap.get(templateType) == null) {
            return defaultTemplate;
        }
        return templateMap.get(templateType);
    }

    private String codeTemplate(Map<String, String> templateMap, String templateType) {
        boolean isBlank = false;
        if (templateMap == null || templateMap.isEmpty()) {
            isBlank = true;
        }
        // 支持用户自定义模板
        switch (templateType) {
            case LAYERED_BASE_MODEL:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_BASE_MODEL,this.baseModelTemplate);
            case LAYERED_MODEL:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_MODEL, this.modelTemplate);
            case LAYERED_DTO:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_DTO, this.dtoTemplate);
            case LAYERED_VALIDATE:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_VALIDATE, this.validateTemplate);
            case LAYERED_SERVICE:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_SERVICE, this.serviceTemplate);
            case LAYERED_CONTROLLER:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_CONTROLLER, this.controllerTemplate);
            case LAYERED_TREE_SERVICE:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_TREE_SERVICE, this.serviceTreeTemplate);
            case LAYERED_TREE_CONTROLLER:
                return getTemplateByType(isBlank, templateMap, this.LAYERED_TREE_CONTROLLER, this.controllerTreeTemplate);
        }

        throw new IllegalArgumentException("template name error.");
    }

    // 根据模板生成分层代码文件到指定的目录
    public void genLayeredCode(String layeredType, String databaseName, String basePackage, String moduleName, String tableName, String tableComment, Map<String, String> templateMap) {
        String className = StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
        String filePath = "";
        String basePath = System.getProperty("user.dir") + String.format("/src/main/java/%s/modules", basePackage.replaceAll("\\.", "/"));
        Kv kv = Kv.by("moduleName", moduleName).set("tableName", tableName).set("tableComment", tableComment).set("basePackage", basePackage);

        switch (layeredType) {
            case LAYERED_BASE_MODEL:
                mkdir(String.format("%s/%s/model/base", basePath, moduleName));
                List<Record> list = Db.find(String.format(BASE_MODEL_SQL, databaseName, tableName));
                kv.set("columnList", list);
                filePath = String.format("%s/%s/model/base/Base%s.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_BASE_MODEL));
                break;

            case LAYERED_MODEL:
                mkdir(String.format("%s/%s/model", basePath, moduleName));
                filePath = String.format("%s/%s/model/%s.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_MODEL));
                break;

            case LAYERED_DTO:
                mkdir(String.format("%s/%s/dto", basePath, moduleName));
                List<Record> dtoList = Db.find(String.format(DTO_SQL, databaseName, tableName));
                kv.set("columnList", dtoList);
                filePath = String.format("%s/%s/dto/%sDTO.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_DTO));
                break;

            case LAYERED_VALIDATE:
                mkdir(String.format("%s/%s/validate", basePath, moduleName));
                List<Record> columnList = Db.find(String.format(DTO_SQL, databaseName, tableName));
                kv.set("columnList", columnList);
                filePath = String.format("%s/%s/validate/%sValidator.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_VALIDATE));
                break;

            case LAYERED_SERVICE:
                mkdir(String.format("%s/%s/service", basePath, moduleName));
                filePath = String.format("%s/%s/service/%sService.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_SERVICE));
                break;

            case LAYERED_CONTROLLER:
                mkdir(String.format("%s/%s/controller", basePath, moduleName));
                kv.set("basePath", tableName.replaceAll("_", "-"));
                filePath = String.format("%s/%s/controller/%sController.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_CONTROLLER));
                break;

            case LAYERED_TREE_SERVICE:
                mkdir(String.format("%s/%s/service", basePath, moduleName));
                filePath = String.format("%s/%s/service/%sService.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_TREE_SERVICE));
                break;

            case LAYERED_TREE_CONTROLLER:
                mkdir(String.format("%s/%s/controller", basePath, moduleName));
                kv.set("basePath", tableName.replaceAll("_", "-"));
                filePath = String.format("%s/%s/controller/%sController.java", basePath, moduleName, className);
                template = engine.getTemplate(codeTemplate(templateMap,this.LAYERED_TREE_CONTROLLER));
                break;
        }

        String codeContent = template.renderToString(kv);

        FileKit.writeFileContent(filePath, codeContent);
    }

    private String tableConditionFormat(String tables) {
        tables = "'" + tables + "'";
        return tables.replaceAll(" ", "").replaceAll(",", "','");
    }

    public void doGenerate(String databaseName, String basePackage, String moduleName, String includeTables, String excludeTables, String[] genLayers, Map<String, String> templateMap) {
        String queryTableSql = String.format(TABLE_SQL, databaseName);
        // 包含的表
        if (StrKit.notBlank(includeTables)) {
            if (includeTables.contains("%")) {
                queryTableSql += String.format(" and table_name like %s", tableConditionFormat(includeTables));
            }
            else {
                queryTableSql += String.format(" and table_name in(%s)", tableConditionFormat(includeTables));
            }
        }
        // 排除的表
        if (StrKit.notBlank(excludeTables)) {
            if (excludeTables.contains("%")) {
                queryTableSql += String.format(" and table_name not like %s", tableConditionFormat(excludeTables));
            }
            else {
                queryTableSql += String.format(" and table_name not in(%s)", tableConditionFormat(excludeTables));
            }
        }

        List<Record> tableList = Db.find(queryTableSql);
        for (Record record : tableList) {
            String tableName = record.getStr("table_name");
            String tableComment = record.getStr("table_comment");

            Record treeTable = Db.findFirst(String.format(IF_TREE_TABLE_SQL, databaseName, tableName));

            if (genLayers.length == 0) {
                genLayers = treeTable == null ? LAYERED_ARRAY : LAYERED_TREE_ARRAY;
            }
            for (String layeredType : genLayers) {
                genLayeredCode(layeredType, databaseName, basePackage, moduleName, tableName, tableComment, templateMap);
            }
        }
    }
}
