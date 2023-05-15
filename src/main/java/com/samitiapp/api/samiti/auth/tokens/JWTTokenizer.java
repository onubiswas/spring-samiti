package com.samitiapp.api.samiti.auth.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.samitiapp.api.samiti.auth.models.TokenPayload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JWTTokenizer {
    private final String secret = "my-secret"; // TODO : remove hardcode

    private final JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();

    public String tokenize(TokenPayload payload) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7*24*60*60*1000); // TODO: remove hardcode, take it from configurations

        return JWT.create()
                .withClaim("id", payload.getUserId())
                .withClaim("phone", payload.getPhone())
                .withClaim("permissions",payload.getPermissions())
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(secret));

    }

    public TokenPayload decodeToken(String token) throws JWTDecodeException {

            DecodedJWT decoded = verifier.verify(token);

            Map<String, Claim> claims = decoded.getClaims();

            TokenPayload payload = new TokenPayload();
            if (null != claims.get("id")) {
                payload.setUserId(claims.get("id").asString());
            }

            if (null != claims.get("phone")) {
                payload.setPhone(claims.get("phone").asString());
            }

            if (null != claims.get("permissions")) {
                payload.setPermissions(claims.get("permissions").asList(String.class));
            }

            return payload;


    }

}
