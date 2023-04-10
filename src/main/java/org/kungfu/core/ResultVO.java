package org.kungfu.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "接口响应结果")
public class ResultVO {
    @ApiModelProperty(value = "状态：成功为200,失败为600~900", example = "200")
    protected Integer code;
    @ApiModelProperty(value = "消息：给出接口返回提示信息", example = "ok")
    protected String msg;
    @ApiModelProperty(value = "数据：存放接口的各种结果数据", example = "object")
    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
