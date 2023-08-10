package com.hanxu.common.exception;

import com.hanxu.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class AException extends RuntimeException{
    private Integer code;
    private String msg;
    public AException(Integer code,String msg){
        super(msg);
        this.code=code;
        this.msg=msg;
    }
    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public AException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "GuliException{" +
                "code=" + code +
                ", message=" + msg +
                '}';
    }
}
