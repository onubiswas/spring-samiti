package com.samitiapp.api.samiti.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;

public class SamitiApiResponse {
    public Object success;
    public HttpStatusCode successCode = HttpStatus.OK;

    public SamitiErrorResponse errors;

    public SamitiApiResponse(Object success) {
        this.success = success;
    }

    public SamitiApiResponse(SamitiErrorResponse errors) {
        this.errors = errors;
    }
}
