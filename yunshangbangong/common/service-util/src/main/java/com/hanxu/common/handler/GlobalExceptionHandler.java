package com.hanxu.common.handler;

import com.hanxu.common.exception.AException;
import com.hanxu.common.result.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;




@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail().message("执行全局异常处理");
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    //特定异常处理
    public Result error(ArithmeticException e) {
        e.printStackTrace();
        return Result.fail().message("执行特定异常处理");
    }

    @ExceptionHandler(AException.class)
    @ResponseBody
    //自定义异常处理
    public Result error(AException e) {
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws  AccessDeniedException{
        return Result.fail().code(205).message("没有操作权限");
    }
}
