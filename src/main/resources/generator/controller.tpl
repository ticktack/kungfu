package #(basePackage).modules.#(moduleName).controller;

#set(className=firstCharToUpperCase(toCamelCase(tableName)))
#set(camelCaseName=toCamelCase(tableName))
#set(tableComment=tableComment.replace("表",""))
import #(basePackage).modules.#(moduleName).dto.#(className)DTO;
import #(basePackage).modules.#(moduleName).model.#(className);
import #(basePackage).modules.#(moduleName).service.#(className)Service;
import com.jfinal.kit.StrKit;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Before;
import com.jfinal.core.Path;
import org.kungfu.core.R;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import org.kungfu.core.*;
import org.kungfu.validator.HeaderValidator;
import org.kungfu.validator.PostRequestValidator;
import com.lastb7.swagger.annotation.ApiResCustom;
import com.lastb7.swagger.enumeration.ApiEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "#(tableComment)", tags = "#(tableComment)接口")
@Path("/#(basePath)")
public class #(className)Controller extends KungfuController {

    @Inject
    private #(className)Service #(camelCaseName)Service;


    @ApiOperation(value = "#(tableComment)信息保存或修改", notes = "根据表单内容保存或更新内容", httpMethod = ApiEnum.METHOD_POST, produces = ApiEnum.PRODUCES_JSON)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "#(camelCaseName)", value = "#(tableComment)信息", dataTypeClass = #(className)DTO.class,  paramType = ApiEnum.PARAM_TYPE_BODY)
    })
    @ApiResCustom(ResultVO.class)
    @Before({PostRequestValidator.class, HeaderValidator.class, EvictInterceptor.class})
    @CacheName("#(camelCaseName)")
    public void saveOrUpdate() {
        UserInfo userInfo = getUserInfo();
        #(className) #(camelCaseName) = toModel(#(className).class);
        CacheKit.removeAll("#(camelCaseName)");
        renderJson(#(camelCaseName)Service.saveOrUpdate(#(camelCaseName), userInfo));
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

        Page<Record> page = #(camelCaseName)Service.page(qc, #(className).class);

        renderJson(R.ok("page", toHumpRecordPage(page)));
    }

    @ApiOperation(value = "#(tableComment)信息查询", notes = "根据表ID查询#(tableComment)信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "#(camelCaseName)Id", value = "#(tableComment)ID", defaultValue = "100")
    })
    @ApiResCustom(ResultVO.class)
    public void getInfo() {
        Long #(camelCaseName)Id = getLong("#(camelCaseName)Id");
        Record record = #(camelCaseName)Service.selectById(#(camelCaseName)Id);
        renderJson(R.ok("#(camelCaseName)", toHump(record)));
    }


    @ApiOperation(value = "删除#(tableComment)记录", notes = "根据表ID删除#(tableComment)记录，支持批量删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "#(camelCaseName)Ids", value = "#(toCamelCase(tableComment))IDs", defaultValue = "100,200,300")
    })
    @ApiResCustom(ResultVO.class)
    public void deleteByIds() {
        String #(camelCaseName)Ids = get("#(camelCaseName)Ids");
        if (StrKit.isBlank(#(camelCaseName)Ids)) {
            renderJson(R.fail("#(toCamelCase(tableComment))IDs为空"));
            return;
        }

        Long[] arr = toArray(#(camelCaseName)Ids, Long::new);

        renderJson(#(camelCaseName)Service.deleteByIds(arr));
    }

}
