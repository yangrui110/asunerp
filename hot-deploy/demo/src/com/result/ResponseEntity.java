package com.result;

/**
 * @author 杨瑞
 * @date 2018-11-23
 * 实体的定义，固定前端访问返回的Json格式
 * */
public class ResponseEntity {
    private Integer code;
    private String msg;
    private Object data;

    /**自定义返回结果码*/
    public ResponseEntity(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    /**
     * 默认msg返回为""
     * */
    public ResponseEntity(Integer code, Object data) {
        this.code = code;
        this.data = data;
        this.msg="";
    }

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
