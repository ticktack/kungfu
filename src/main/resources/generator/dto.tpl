package #(basePackage).modules.#(moduleName).dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

@ApiModel("#(tableComment)DTO")
public class #(firstCharToUpperCase(toCamelCase(tableName)))DTO {
    #for(x : columnList)
    @ApiModelProperty(value = "#(notBlank(x.column_comment)?x.column_comment:'暂无注释')", example = "#(toExample(x.data_type))", position= #(for.count)#if(x.is_nullable == 'NO'), required = true#end)
    private #(toJavaType(x.data_type,x.column_type)) #(toCamelCase(x.column_name));
    #end

    #for(x : columnList)
    public #(toJavaType(x.data_type,x.column_type)) get#(firstCharToUpperCase(toCamelCase(x.column_name)))() {
      return #(toCamelCase(x.column_name));
    }
    public void set#(firstCharToUpperCase(toCamelCase(x.column_name)))(#(toJavaType(x.data_type,x.column_type)) #(toCamelCase(x.column_name))) {
      this.#(toCamelCase(x.column_name)) = #(toCamelCase(x.column_name));
    }

    #end
}
