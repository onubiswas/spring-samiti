package com.samitiapp.api.samiti.auth.models;

import lombok.Data;



@Data
public class UserAccount {

    public static String table = "spring_auth_accounts";

    private String id;
    private String name;
    private String phone;

    private Long createdAt;

    private Long updatedAt;

    public UserAccount(String id) {
        this.id = id;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UserAccount() {}

}

