package com.carrot.yygh.common.exception;

import com.carrot.yygh.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义全局异常类
 *
 * @author carrot
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class AppointmentException extends RuntimeException {

    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    @ApiModelProperty(value = "异常状态")
    private ResultCodeEnum codeEnum;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public AppointmentException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public AppointmentException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.codeEnum = resultCodeEnum;
    }

    @Override
    public String toString() {
        return "HospitalException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
