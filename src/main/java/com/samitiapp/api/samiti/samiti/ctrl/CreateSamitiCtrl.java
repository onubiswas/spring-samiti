package com.samitiapp.api.samiti.samiti.ctrl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.samitiapp.api.samiti.auth.models.TokenPayload;
import com.samitiapp.api.samiti.auth.tokens.JWTTokenizer;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import com.samitiapp.api.samiti.samiti.models.Samiti;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Log
@Component
public class CreateSamitiCtrl {

    @Autowired
    JWTTokenizer tokenizer;

    @Autowired
    Firestore db;



    @Getter
    @Setter
    public class CreateSamitiRequest {
        private String samitiName;

        @JsonIgnore
        private String token;

        @JsonIgnore
        private TokenPayload decoded;
    }

    @Data
    @Builder
    public static class CreateSamitiResponse  {
        private String id;
        private String name;

        private long createdAt;

    }

    private SamitiErrorResponse permit(CreateSamitiRequest r) {
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

    public SamitiApiResponse createSamiti(CreateSamitiRequest r) throws ExecutionException, InterruptedException {

        String userId = r.getDecoded().getUserId();

        Samiti samiti = new Samiti(UUID.randomUUID().toString(), r.getSamitiName(), userId);

        ApiFuture<WriteResult> result = db.collection(Samiti.table).document(samiti.getId()).set(samiti);

        result.get();

        return new SamitiApiResponse(CreateSamitiResponse.builder()
                .id(samiti.getId())
                .name(samiti.getName())
                .createdAt(samiti.getCreatedAt())
                .build());
    }

    public SamitiApiResponse run(CreateSamitiRequest r, String token) throws InterruptedException, ExecutionException {

        SamitiErrorResponse err = permit(r);
        if (null != err) {
            log.info("permission check failed");
            return new SamitiApiResponse(err);
        }
        return createSamiti(r);


    }

}
