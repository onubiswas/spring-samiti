package com.samitiapp.api.samiti.auth.handler;

import com.samitiapp.api.samiti.auth.ctrl.LoginCtrl;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Log
public class LoginHandler {

    @Autowired
    private LoginCtrl ctrl;

    @PostMapping(path = "/v1/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginCtrl.LoginRequest body) throws ExecutionException, InterruptedException {
        log.info("login request start");

        SamitiApiResponse response = ctrl.run(body);

        log.info("login request end");
        if(response.errors != null) {
            return new ResponseEntity<>(response.errors, response.errors.getStatusCode());
        }
        return new ResponseEntity<>(response.success, response.successCode);
    }



}
