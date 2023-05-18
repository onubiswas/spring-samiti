package com.samitiapp.api.samiti.auth.ctrl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import com.samitiapp.api.samiti.auth.models.TokenPayload;
import com.samitiapp.api.samiti.auth.models.UserAccount;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import com.samitiapp.api.samiti.auth.tokens.JWTTokenizer;

import lombok.Data;
import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Component
@Log
public class UpdateProfileCtrl {

    @Autowired
    JWTTokenizer tokenizer;

    @Autowired
    Firestore db;


    public SamitiApiResponse run(UpdateProfileRequest r) throws ExecutionException, InterruptedException {
        SamitiErrorResponse err = permit(r);
        if (null != err) {
            log.info("permission check failed");
            return new SamitiApiResponse(err);
        }

        return updateProfile(r);
    }

    private SamitiApiResponse updateProfile(UpdateProfileRequest r) throws ExecutionException, InterruptedException {
        try {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("name", r.name);
            updates.put("updatedAt", System.currentTimeMillis());


            ApiFuture<WriteResult> promise = db.collection(UserAccount.table)
                    .document(r.decoded.getUserId())
                    .update(updates);

            promise.get(); // block until done

            return new SamitiApiResponse(new UpdateProfileResponse());


        } catch (Exception e) {

            // internal server error
            throw e;
        }

    }
    private SamitiApiResponse userNotRegisteredError() {
        SamitiErrorResponse err = SamitiErrorResponse.builder().build();
        err.setMessage(String.format("user is not registered"));
        err.setAppcode(ErrorCodes.USER_NOT_EXIST);
        return new SamitiApiResponse(err);
    }

    private SamitiErrorResponse permit(UpdateProfileRequest r) {
        log.info("permission check start");
        try {
            TokenPayload decoded = tokenizer.decodeToken(r.getToken());
            
            r.setDecoded(decoded);
        } catch (JWTVerificationException e) {
            log.info("token validation failed: invalid jwt token");
            return SamitiErrorResponse.builder()
                    .statusCode(HttpStatus.FORBIDDEN)
                    .appcode(ErrorCodes.OPERATION_NOT_PERMITTED)
                    .message("not authorized")
                    .build();
        } catch (Exception e)  {
            throw e;
        }

        log.info("permission check finish");

        return null;
    }

    @Data
    public static class UpdateProfileRequest {
        String name;

        @JsonIgnore
        private String token;

        @JsonIgnore
        private TokenPayload decoded;

    }

    @Data
    public static class UpdateProfileResponse {
        private String status;

        public UpdateProfileResponse() {
            this.status = "ok" ;
        }
    }

}
