package com.samitiapp.api.samiti.auth.handler;


import com.samitiapp.api.samiti.auth.ctrl.UpdateProfileCtrl;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Log
public class UpdateProfileHandler {
    @Autowired
    UpdateProfileCtrl ctrl;

    @PostMapping(value = "/v1/profile/update",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> updateProfile(@RequestBody UpdateProfileCtrl.UpdateProfileRequest r, @RequestHeader(required = true, name = "Authorization") String token) throws ExecutionException, InterruptedException {
        log.info("update profile request start");
        r.setToken(token);
        SamitiApiResponse response = ctrl.run(r);

        log.info("update profile request ends");

        if(response.errors != null) {
            return new ResponseEntity<>(response.errors, response.errors.getStatusCode());
        }

        return new ResponseEntity<>(response.success, response.successCode);
    }
}
