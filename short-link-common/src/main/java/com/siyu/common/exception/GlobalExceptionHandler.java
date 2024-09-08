package com.siyu.common.exception;

import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult exceptions = e.getBindingResult();
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                FieldError fieldError = (FieldError) errors.get(0);
                return R.fail(ErrorStatus.PARAM_ERROR, "参数校验失败[字段 " + fieldError.getField() + " " + fieldError.getDefaultMessage() + "]");
            }
        }
        return R.fail(ErrorStatus.PARAM_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public R<String> businessException(BusinessException e) {
        return R.fail(ErrorStatus.resolve(e.getCode()), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R<String> Exception(Exception e) {
        log.warn(e.toString());
        return R.fail(ErrorStatus.SYS_ERROR);
    }

}
