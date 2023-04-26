package #(basePackage).modules.#(moduleName).validate;

#set(className=firstCharToUpperCase(toCamelCase(tableName)))
#set(camelCaseName=toCamelCase(tableName))
import #(basePackage).modules.#(moduleName).model.#(className);
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import org.kungfu.core.KungfuConstant;
import org.kungfu.util.KungfuKit;

public class #(className)Validator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        setRet(Ret.fail().set("code", 680));

        String json = c.getAttr(KungfuConstant.JSON_REQUEST_BODY);
        try {
            #(className) #(camelCaseName) = KungfuKit.toModelValidator(json, #(className).class);

            if (#(camelCaseName) == null) {
                addError(KungfuConstant.MASSAGE, "#(className)对象不能为空");
            }
            else {
                #for(column : columnList)
                #set(columnName=(toCamelCase(column.column_name)))
                #set(upperCaseColumnName=firstCharToUpperCase(toCamelCase(column.column_name)))
                #set(javaType=toJavaType(column.data_type, column.column_type))
                #set(javaMethod=toJavaMethod(column.data_type, column.column_type))
                #if("NO".equals(column.is_nullable) && !"id".equals(column.column_name) && !"pinyin".equals(column.column_name) && (column.column_default == null || ("0".equals(column.column_default) && "bigint".equals(column.data_type))))
                #if("varchar".equals(column.data_type))
                if (StrKit.isBlank(#(camelCaseName).get#(upperCaseColumnName)())) {
                    addError(KungfuConstant.MASSAGE, "#(className)对象属性#(columnName)不能为空");
                }
                #else
                if (#(camelCaseName).get#(upperCaseColumnName)() == null) {
                    addError(KungfuConstant.MASSAGE, "#(className)对象属性#(columnName)不能为空");
                }
                #end
                #end
                #end
            }
        } catch (Exception e) {
            addError(KungfuConstant.MASSAGE, e.getMessage());
        }
    }

    @Override
    protected void handleError(Controller c) {
        c.renderJson(getRet());
    }
}
