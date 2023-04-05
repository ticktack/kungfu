package #(basePackage).modules.#(moduleName).dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

@ApiModel("#(tableComment)DTO")
public class #(firstCharToUpperCase(toCamelCase(tableName)))DTO {
    #for(column : columnList)
    @ApiModelProperty(value = "#(notBlank(column.column_comment)?column.column_comment:'暂无注释')", example = "#(toExample(column.data_type))", position= #(for.count)#if(column.is_nullable == 'NO'), required = true#end)
    private #(toJavaType(column.data_type,column.column_type)) #(toCamelCase(column.column_name));
    #end

    #for(column : columnList)
    public #(toJavaType(column.data_type,column.column_type)) get#(firstCharToUpperCase(toCamelCase(column.column_name)))() {
      return #(toCamelCase(column.column_name));
    }
    public void set#(firstCharToUpperCase(toCamelCase(column.column_name)))(#(toJavaType(column.data_type,column.column_type)) #(toCamelCase(column.column_name))) {
      this.#(toCamelCase(column.column_name)) = #(toCamelCase(column.column_name));
    }

    #end
}
