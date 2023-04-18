package org.kungfu.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 {
     "pageNumber": 1,
     "pageSize": 20,
     "orderColumnName": "create_time",
     "orderBy": "desc",
     "modelMap": {
         "moduleId": 70,
         "projectId": 62,
         "tableName": "children_main",
         "tableComment": "左树"
     },
     "queryTypeMap": {
         "moduleId": "eq",
         "projectId": "eq",
         "tableName": "like",
         "tableComment": "like"
     }
 }
 */
@ApiModel("查询条件")
public class QueryCondition {
    @ApiModelProperty(value = "分页页码", example = "1", position=1, required = true)
    private Integer pageNumber;
    @ApiModelProperty(value = "分页条数", example = "20", position=2, required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "模型字段Map", example = "{'moduleId': 70,'projectId': 62,'tableName': 'children_main','tableComment': '左树'}", position=5, required = true)
    private Map<String, Object> modelMap;
    @ApiModelProperty(value = "查询类型Map", example = "{'moduleId': 'eq','projectId': 'eq','tableName': 'like','tableComment': 'like_left'}", position=6, required = true)
    private Map<String, String> queryTypeMap;
    @ApiModelProperty(value = "排序列名", example = "create_time", position=3, required = true)
    private String orderColumnName;
    @ApiModelProperty(value = "排序方式", example = "desc", position=4, required = true)
    private String orderBy;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Map<String, Object> getModelMap() {
        return modelMap;
    }

    public void setModelMap(Map<String, Object> modelMap) {
        this.modelMap = modelMap;
    }

    public Map<String, String> getQueryTypeMap() {
        return queryTypeMap;
    }

    public void setQueryTypeMap(Map<String, String> queryTypeMap) {
        this.queryTypeMap = queryTypeMap;
    }

    public String getOrderColumnName() {
        return orderColumnName;
    }

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
