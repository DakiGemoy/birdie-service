package com.priv.birdie.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse<D> {
    private String code;
    private String message;
    private D data;
}
