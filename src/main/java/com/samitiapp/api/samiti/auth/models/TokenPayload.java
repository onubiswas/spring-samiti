package com.samitiapp.api.samiti.auth.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TokenPayload {
    String userId;
    String phone;
    List<String> permissions = new ArrayList<>();

}
