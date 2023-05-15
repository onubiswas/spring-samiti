package com.samitiapp.api.samiti.auth.handler;

import com.samitiapp.api.samiti.auth.ctrl.ProfileCtrl;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Log
public class Profile {

    @Autowired
    ProfileCtrl ctrl;
    @GetMapping(value = "/v1/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?>  getProfile(@RequestHeader(required = true, name = "Authorization") String token) throws ExecutionException, InterruptedException {
        log.info("get profile request start");
        SamitiApiResponse response = ctrl.run(token);
        System.out.println("token = "+ token);
        log.info("get profile request ends");

        if (response.errors != null) {
            return new ResponseEntity<>(response.errors, response.errors.getStatusCode());
        }

        return new ResponseEntity<>(response.success, response.successCode);
    }



}
