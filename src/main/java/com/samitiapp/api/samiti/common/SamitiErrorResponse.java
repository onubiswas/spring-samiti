package com.samitiapp.api.samiti.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;


public class SamitiErrorResponse {
    private String message;
    private HashMap<String, String> errors;
    private int appcode;
    private HttpStatusCode statusCode;

    SamitiErrorResponse(final String message, final HashMap<String, String> errors, final int appcode, final HttpStatusCode statusCode) {
        this.message = message;
        this.errors = errors;
        this.appcode = appcode;
        this.statusCode = statusCode;
    }

    public static SamitiErrorResponseBuilder builder() {
        return new SamitiErrorResponseBuilder();
    }

    public String getMessage() {
        return this.message;
    }

    public HashMap<String, String> getErrors() {
        return this.errors;
    }

    public int getAppcode() {
        return this.appcode;
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setErrors(final HashMap<String, String> errors) {
        this.errors = errors;
    }

    public void setAppcode(final int appcode) {
        this.appcode = appcode;
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static class SamitiErrorResponseBuilder {
        private String message;
        private HashMap<String, String> errors;
        private int appcode;
        private HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        SamitiErrorResponseBuilder() {
        }

        public SamitiErrorResponseBuilder message(final String message) {
            this.message = message;
            return this;
        }

        public SamitiErrorResponseBuilder errors(final HashMap<String, String> errors) {
            this.errors = errors;
            return this;
        }

        public SamitiErrorResponseBuilder appcode(final int appcode) {
            this.appcode = appcode;
            return this;
        }

        public SamitiErrorResponseBuilder statusCode(final HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public SamitiErrorResponse build() {
            return new SamitiErrorResponse(this.message, this.errors, this.appcode, this.statusCode);
        }

        public String toString() {
            return "SamitiErrorResponse.SamitiErrorResponseBuilder(message=" + this.message + ", errors=" + this.errors + ", appcode=" + this.appcode + ", statusCode=" + this.statusCode + ")";
        }
    }
}
