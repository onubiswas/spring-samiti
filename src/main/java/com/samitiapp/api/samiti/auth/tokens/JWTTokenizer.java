package com.samitiapp.api.samiti.auth.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.samitiapp.api.samiti.auth.models.TokenPayload;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTTokenizer {
    private final String secret = "my-secret"; // TODO : remove hardcode

    public String tokenize(TokenPayload payload) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7*24*60*60*1000); // TODO: remove hardcode, take it from configurations

        String token = JWT.create()
                .withClaim("id", payload.getUserId())
                .withClaim("phone", payload.getPhone())
                .withClaim("permissions",payload.getPermissions())
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(secret));

        return token;


    }

}
