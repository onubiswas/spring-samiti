package com.samitiapp.api.samiti.auth.models;

import lombok.Data;

import java.util.UUID;
@Data
public class PhoneMapping {

    public static String table = "spring_auth_phones";

    private String id;
    private String phone;

    public PhoneMapping(String phone) {
        this.phone = phone;
    }
}
