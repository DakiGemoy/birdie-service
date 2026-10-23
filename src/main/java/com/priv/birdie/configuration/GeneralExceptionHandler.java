package com.priv.birdie.configuration;

import com.priv.birdie.model.response.BaseResponse;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

import static com.priv.birdie.model.Constant.RespCode.*;

@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(
                        BaseResponse.<String>builder()
                                .code(BAD_REQUEST)
                                .message("Bad Request")
                                .data(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<BaseResponse<Map<String,String>>> handleCustomException(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException mex){
            mex.getBindingResult().getFieldErrors()
                    .forEach(e -> errors.put(e.getField(), "Can't be empty"));
        } else {
            ((BindException) ex).getBindingResult().getFieldErrors()
                    .forEach(e -> errors.put(e.getField(), "Can't be empty"));
        }

        return ResponseEntity.badRequest()
                .body(
                        BaseResponse.<Map<String,String>>builder()
                                .message(BAD_REQUEST)
                                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                .data(errors)
                                .build()
                );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Map<String,String>>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        BaseResponse.<Map<String,String>>builder()
                                .code(BAD_REQUEST)
                                .message("Missing required parameter")
                                .data(
                                        Map.of("name",ex.getParameterName())
                                ).build()
                );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BaseResponse<Map<String,String>>> handleMissingParameter(MissingServletRequestPartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        BaseResponse.<Map<String,String>>builder()
                                .code(BAD_REQUEST)
                                .message("Missing required parameter")
                                .data(
                                        Map.of("name",ex.getRequestPartName())
                                ).build()
                );
    }

    @ExceptionHandler(ProcessException.class)
    public ResponseEntity<BaseResponse<Void>> handleCustomException(ProcessException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(
                        BaseResponse.<Void>builder()
                                .code(
                                        ex.getCode()!=null ? ex.getCode() : String.valueOf(ex.getStatus().value())
                                )
                                .message(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler({
            Exception.class,
            RuntimeException.class})
    public ResponseEntity<BaseResponse<Void>> handleBaseException(Exception ex) {
        return ResponseEntity.status(500)
                .body(
                        BaseResponse.<Void>builder()
                                .code(INTERNAL_ERROR)
                                .message("Internal Server Error")
                                .build()
                );
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<BaseResponse<Void>> handlerNotUniqueData(IncorrectResultSizeDataAccessException ex) {
        return ResponseEntity.status(500)
                .body(
                        BaseResponse.<Void>builder()
                                .code(ERROR_DATABASE)
                                .message("Error data result set")
                                .build()
                );
    }
}
