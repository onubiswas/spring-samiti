package com.samitiapp.api.samiti.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;


public class SamitiErrorResponse {
    private String status;

    private HashMap<String, String> errors;

    private int appcode;

    private HttpStatusCode statusCode;

    public SamitiErrorResponse() {
        this.statusCode = HttpStatus.BAD_REQUEST;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public int getAppcode() {
        return appcode;
    }

    public void setAppcode(int appcode) {
        this.appcode = appcode;
    }
}
