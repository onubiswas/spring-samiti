package com.samitiapp.api.samiti.auth.ctrl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.samitiapp.api.samiti.auth.models.PhoneMapping;
import com.samitiapp.api.samiti.auth.models.TokenPayload;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import com.samitiapp.api.samiti.auth.tokens.JWTTokenizer;

import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Log
@Component
public class LoginCtrl {

    @Autowired
    Firestore db;


    @Autowired
    JWTTokenizer jwtTokenizer;



    public SamitiApiResponse run(LoginRequest r) throws ExecutionException, InterruptedException {
        SamitiErrorResponse err = validate(r);
        if(err != null){
            log.info("request body validation has failed");
        }

        return login(r);
    }

    private SamitiApiResponse login(LoginRequest r) throws ExecutionException, InterruptedException {

        SamitiErrorResponse err = checkOtp(r);
        if (err != null) {
            return new SamitiApiResponse(err);
        }


        TokenPayload payload = new TokenPayload();
        payload.setUserId(r.getUserId());
        payload.setPhone(r.getPhone());


        String token = jwtTokenizer.tokenize(payload);

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return new SamitiApiResponse(response);
    }


    private SamitiErrorResponse checkOtp(LoginRequest r) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<DocumentSnapshot> promise = db.collection(PhoneMapping.table)
                    .document(r.getPhone())
                    .get();

            DocumentSnapshot doc = promise.get(); // wait for the request to end

            PhoneMapping pm = doc.toObject(PhoneMapping.class);

            // check
            if (!r.getOtp().equals(pm.getOtp())) {
                SamitiErrorResponse err = new SamitiErrorResponse();
                err.setMessage("otp does not match");
                err.setAppcode(ErrorCodes.INVALID_OTP);
               return err;
            }

            // check if expired
            if (pm.getOtpExpiresAt() < System.currentTimeMillis()) {
                SamitiErrorResponse err = new SamitiErrorResponse();
                err.setMessage("otp expired");
                err.setAppcode(ErrorCodes.EXPIRED_OTP);
                return err;
            }

            r.setMp(pm);
            r.setUserId(pm.getId());


        } catch (Exception e) {
            if (ExceptionUtils.indexOfType(e, NotFoundException.class) != -1) {
                return phoneNotRegisteredError(r).errors;
            }
            // internal server error
            throw e;
        }



        return null;

    }
    private SamitiApiResponse otpNotFoundError(LoginRequest r) {
        SamitiErrorResponse err = new SamitiErrorResponse();
        err.setMessage(String.format("invalid otp"));
        err.setAppcode(ErrorCodes.INVALID_OTP);
        return new SamitiApiResponse(err);

    }
    private SamitiApiResponse phoneNotRegisteredError(LoginRequest r) {
        SamitiErrorResponse err = new SamitiErrorResponse();
        err.setMessage(String.format("phone number %s is not registered", r.getPhone()));
        err.setAppcode(ErrorCodes.PHONE_NOT_REGISTERED);
        return new SamitiApiResponse(err);

    }

    private SamitiErrorResponse validate(LoginRequest r) {
        log.info("request body validation start");
        if(r.phone.length() != 10) {
            log.info("request body validation failed");
            SamitiErrorResponse err = new SamitiErrorResponse();
            HashMap<String, String> errors = new HashMap<>();
            errors.put("phone", "invalid phone number");
            err.setErrors(errors);
            err.setMessage("invalid request body");
            err.setAppcode(ErrorCodes.INVALID_REQUEST_BODY);
            return err;
        }

        log.info("request body validation successful");

        return null;
    }

    @Data
    public static class LoginResponse {
        public String token;

    }

    @Data
    public static class LoginRequest {
        private String phone;
        private String otp;

        @JsonIgnore
        private String userId;

        @JsonIgnore
        private String name;

        @JsonIgnore
        private PhoneMapping mp;


    }

}
