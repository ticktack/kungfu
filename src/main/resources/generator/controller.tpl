package #(basePackage).modules.#(moduleName).controller;

import #(basePackage).modules.#(moduleName).dto.#(firstCharToUpperCase(toCamelCase(tableName)))DTO;
import #(basePackage).modules.#(moduleName).model.#(firstCharToUpperCase(toCamelCase(tableName)));
import #(basePackage).modules.#(moduleName).service.#(firstCharToUpperCase(toCamelCase(tableName)))Service;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Before;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.kungfu.core.*;
import com.lastb7.swagger.annotation.ApiResCustom;
import com.lastb7.swagger.enumeration.ApiEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "#(tableComment)", tags = "#(tableComment)接口")
@Path("/#(basePath)")
public class #(firstCharToUpperCase(toCamelCase(tableName)))Controller extends KungfuController {

    @Inject
    private #(firstCharToUpperCase(toCamelCase(tableName)))Service #(toCamelCase(tableName))Service;


    @ApiOperation(value = "#(tableComment)信息保存或修改", notes = "根据表单内容保存或更新内容", httpMethod = ApiEnum.METHOD_POST, produces = ApiEnum.PRODUCES_JSON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "操作人姓名", paramType = ApiEnum.PARAM_TYPE_BODY),
            @ApiImplicitParam(name = "#(toCamelCase(tableName))", value = "#(tableComment)信息", dataTypeClass = #(firstCharToUpperCase(toCamelCase(tableName)))DTO.class,  paramType = ApiEnum.PARAM_TYPE_BODY)
    })
    @ApiResCustom(ResultVO.class)
    @Before(PostRequestValidator.class)
    public void saveOrUpdate() {
        String userName = toStr("userName");
        #(firstCharToUpperCase(toCamelCase(tableName))) #(toCamelCase(tableName)) = toModel("#(toCamelCase(tableName))", #(firstCharToUpperCase(toCamelCase(tableName))).class);

        renderJson(#(toCamelCase(tableName))Service.saveOrUpdate(#(toCamelCase(tableName)), userName));
    }


    @ApiOperation(value = "分页查询", notes = "根据页码及查询条件分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryCondition", value = "查询条件", dataTypeClass = QueryCondition.class, paramType = ApiEnum.PARAM_TYPE_BODY, required = true)
    })
    @ApiResCustom(ResultVO.class)
    @Before(PostRequestValidator.class)
    public void queryPage() {

        QueryCondition qc = toDTO(QueryCondition.class);

        qc = convention(qc, KungfuConstant.QUERY_TYPE_PAGE);

        Page<Record> page = #(toCamelCase(tableName))Service.page(qc, #(firstCharToUpperCase(toCamelCase(tableName))).class);

        renderJson(toHumpRecordPage(page));
    }

    @ApiOperation(value = "#(tableComment)信息查询", notes = "根据表ID查询#(tableComment)信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "#(toCamelCase(tableName))Id", value = "#(tableComment)ID", defaultValue = "100")
    })
    @ApiResCustom(ResultVO.class)
    public void getInfo() {
        Long #(toCamelCase(tableName))Id = getLong("#(toCamelCase(tableName))Id");
        Record record = #(toCamelCase(tableName))Service.selectById(#(toCamelCase(tableName))Id);
        renderJson(Ret.ok("#(toCamelCase(tableName))", toHump(record)));
    }


    @ApiOperation(value = "删除#(tableComment)记录", notes = "根据表ID删除#(tableComment)记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "#(toCamelCase(tableName))Id", value = "#(toCamelCase(tableComment))ID", defaultValue = "100")
    })
    @ApiResCustom(ResultVO.class)
    public void deleteById() {
        Long #(toCamelCase(tableName))Id = getLong("#(toCamelCase(tableName))Id");

        renderJson(#(toCamelCase(tableName))Service.deleteById(#(toCamelCase(tableName))Id));
    }

}
