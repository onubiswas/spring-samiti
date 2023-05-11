package com.samitiapp.api.samiti.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;


@Getter
@Setter
public class SamitiErrorResponse {
    private String message;

    private HashMap<String, String> errors;

    private int appcode;

    private HttpStatusCode statusCode;

    public SamitiErrorResponse() {
        this.statusCode = HttpStatus.BAD_REQUEST;
    }

}
