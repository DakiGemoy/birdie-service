package com.priv.birdie.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessException extends RuntimeException {

    private HttpStatus status;
    private String code;

    public ProcessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.code = String.valueOf(status.value());
    }

    public ProcessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
