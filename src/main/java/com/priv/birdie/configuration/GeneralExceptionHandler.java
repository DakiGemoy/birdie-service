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

@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(
                        BaseResponse.<String>builder()
                                .code("04")
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
                    .forEach(e -> errors.put(e.getField(), "Tidak boleh kosong"));
        } else {
            ((BindException) ex).getBindingResult().getFieldErrors()
                    .forEach(e -> errors.put(e.getField(), "Tidak boleh kosong"));
        }

        return ResponseEntity.badRequest()
                .body(
                        BaseResponse.<Map<String,String>>builder()
                                .message("Bad Request")
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
                                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
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
                                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
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
                                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                                .message("Internal Server Error")
                                .build()
                );
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<BaseResponse<Void>> handlerNotUniqueData(IncorrectResultSizeDataAccessException ex) {
        return ResponseEntity.status(500)
                .body(
                        BaseResponse.<Void>builder()
                                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                                .message("Hasil pencarian data tidak sesuai")
                                .build()
                );
    }
}
