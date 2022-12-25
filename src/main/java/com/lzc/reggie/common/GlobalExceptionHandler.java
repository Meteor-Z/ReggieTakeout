package com.lzc.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局捕获这种异常
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class}) // 这里就是拦截发生的异常
@ResponseBody
@Slf4j
public class GlobalExceptionHandler
{
    /**
     * 处理 SQL 无法处理的的异常
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> expectionHandler(SQLIntegrityConstraintViolationException exception)
    {
        boolean isContain = exception.getMessage().contains("Duplicate entry");
        if (isContain)
        {
            return R.error("该员工已经存在");
        } else
        {
            return R.error("未知错误");
        }


    }
}
