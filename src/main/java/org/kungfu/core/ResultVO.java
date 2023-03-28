package org.kungfu.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "接口响应结果")
public class ResultVO {
    @ApiModelProperty(value = "状态：成功为ok,失败为fail", example = "ok")
    protected String state;
    @ApiModelProperty(value = "消息：给出接口返回提示信息", example = "success")
    protected String msg;
    @ApiModelProperty(value = "数据：存放接口的各种结果数据", example = "object data")
    private Object data;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
