package com.carrot.yygh.common.exception;

import com.carrot.yygh.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(AppointmentException.class)
    @ResponseBody
    public Result appointmentError(AppointmentException e) {
        e.printStackTrace();
        return Result.fail(e.getCodeEnum(), null);
    }
}
