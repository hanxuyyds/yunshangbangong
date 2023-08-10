package com.hanxu.common.result;

import lombok.Data;

@Data
public class Result<T> {
    //状态码
    private Integer code;
    //返回信息
    private String message;
    //数据
    private T data;

    //私有化
    private Result(){}
    //封装返回数据
    public static <T> Result<T> build(T body,ResultCodeEnum resultCodeEnum){
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    //成功
    public static<T> Result<T> ok(){
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
    public static<T> Result<T> ok(T data){
        return Result.build(data,ResultCodeEnum.SUCCESS);
    }
    //失败
    public static<T> Result<T> fail(){
        return Result.build(null,ResultCodeEnum.FAIL);
    }
    public static<T> Result<T> fail(T data){
        return Result.build(data,ResultCodeEnum.FAIL);
    }


    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
