package com.atguigu.common.exception;



import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@RestControllerAdvice
public class UnifiedExceptionHandler {
    /**
     * 未定义异常
     */
    @ExceptionHandler(value = Exception.class) //当controller中抛出Exception，则捕获
    public R handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.error();
    }
    /**
     * 自定义异常
     */
    @ExceptionHandler(value = BadSqlGrammarException.class) //当controller中抛出Exception，则捕获
    public R handleException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
       return   R.error().setResult(ResponseEnum.BAD_SQL_GRAMMAR_ERROR);
    }
    @ExceptionHandler(value = {BusinessException.class}) //当controller中抛出Exception，则捕获
    public R handleException(BusinessException e) {
        log.error(e.getMessage(), e);
        return   R.error().message(e.getMessage()).code(e.getCode());
    }
}
