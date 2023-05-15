package com.samitiapp.api.samiti.auth.ctrl;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.samitiapp.api.samiti.auth.models.TokenPayload;
import com.samitiapp.api.samiti.auth.models.UserAccount;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.auth.tokens.JWTTokenizer;

import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;



@Component
@Log
public class ProfileCtrl {

    @Autowired
    Firestore db;

    @Autowired
    JWTTokenizer tokenizer;


    public SamitiApiResponse run(String token) throws ExecutionException, InterruptedException {
        ApiRequest r = ApiRequest.builder().token(token).build();

        SamitiErrorResponse err = permit(r);
        if (null != err) {
            log.info("permission check failed");
            return new SamitiApiResponse(err);
        }

        return myProfile(r);

    }

    private SamitiApiResponse myProfile(ApiRequest r) throws ExecutionException, InterruptedException {
        try {
            ApiFuture<DocumentSnapshot> promise = db.collection(UserAccount.table)
                    .document(r.decoded.getUserId())
                    .get();
            DocumentSnapshot doc = promise.get(); // wait for the request to end

            return new SamitiApiResponse(doc.toObject(UserAccount.class));

        } catch (Exception e) {
            if (ExceptionUtils.indexOfType(e, NotFoundException.class) != -1) {
                return userNotRegisteredError();
            }
            // internal server error
            throw e;
        }

    }
    private SamitiErrorResponse permit(ApiRequest r) {
        log.info("permission check start");
        try {
            TokenPayload decoded = tokenizer.decodeToken(r.getToken());

            System.out.println("decoded token is");
            System.out.println(decoded.getPhone());
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

    @Getter
    @Setter
    @Builder
    public static class ApiRequest  {
        @JsonIgnore
        private String token;

        @JsonIgnore
        private TokenPayload decoded;
    }



    private SamitiApiResponse userNotRegisteredError() {
        SamitiErrorResponse err = SamitiErrorResponse.builder().build();
        err.setMessage(String.format("user is not registered"));
        err.setAppcode(ErrorCodes.USER_NOT_EXIST);
        return new SamitiApiResponse(err);
    }
}
