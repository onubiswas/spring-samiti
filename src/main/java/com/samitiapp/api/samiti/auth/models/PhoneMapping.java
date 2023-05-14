package com.samitiapp.api.samiti.auth.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;
@Data
public class PhoneMapping {

    public static String table = "spring_auth_phones";

    private String id;
    private String phone;

    private String otp;

    private long otpExpiresAt;

    private long createdAt;

    public PhoneMapping() {
    }


    public PhoneMapping(String phone) {
        this.phone = phone;
    }
}
