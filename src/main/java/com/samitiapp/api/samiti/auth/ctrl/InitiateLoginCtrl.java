package com.samitiapp.api.samiti.auth.ctrl;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firestore.v1.Write;
import com.samitiapp.api.samiti.auth.models.PhoneMapping;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import com.samitiapp.api.samiti.db.DbProvider;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


@Component
@Log
public class InitiateLoginCtrl {

    @Autowired
    Firestore db;

    public SamitiErrorResponse validate(InitiateLoginRequest body)  {
        log.info("request body validation start");
        if(!body.validate()) {
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
    public SamitiApiResponse initiateLogin(InitiateLoginRequest body) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<DocumentSnapshot> promise = db.collection(PhoneMapping.table)
                    .document(body.getPhone())
                    .get();

            DocumentSnapshot doc = promise.get(); // wait for the request to end

//            PhoneMapping pm = doc.toObject(PhoneMapping.class);
        } catch (Exception e) {
            if (ExceptionUtils.indexOfType(e, NotFoundException.class) != -1) {
                return phoneNotRegisteredError(body);
            }
            // internal server error
            throw e;
        }

        try {

            String otp = "1234"; // TODO: generate unique otp
            long expiryTs = System.currentTimeMillis();
            expiryTs += 5 * 60 * 1000; // 5 minutes

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("otp", otp);
            updates.put("otpExpiresAt", expiryTs);

            ApiFuture<WriteResult> promise = db.collection(PhoneMapping.table)
                    .document(body.getPhone())
                    .update(updates);

            promise.get(); // block until done




        } catch (Exception e) {
            if (ExceptionUtils.indexOfType(e, NotFoundException.class) != -1) {
                return phoneNotRegisteredError(body);
            }
            // internal server error
            throw e;
        }

        return new SamitiApiResponse(InitiateLoginResponse.ok());

    }

    private SamitiApiResponse phoneNotRegisteredError(InitiateLoginRequest r) {
        SamitiErrorResponse err = new SamitiErrorResponse();
        err.setMessage(String.format("phone number %s is not registered", r.getPhone()));
        err.setAppcode(ErrorCodes.PHONE_NOT_REGISTERED);
        return new SamitiApiResponse(err);

    }

    public SamitiApiResponse run(InitiateLoginRequest r) throws ExecutionException, InterruptedException {
        SamitiErrorResponse err = validate(r);
        if(null != err) {
            log.info("request body validation has failed");
            return new SamitiApiResponse(err);
        }
        return initiateLogin(r);
    }
    @Data
    public static class InitiateLoginResponse {
        public String message;

        public static InitiateLoginResponse ok() {
            InitiateLoginResponse ok = new InitiateLoginResponse();
            ok.message = "ok";
            return ok;
        }
    }

    @Data
    public static class InitiateLoginRequest {
        public String phone;

        public boolean validate() {
           return phone.length() == 10;
        }
    }
}
