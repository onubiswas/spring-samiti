package com.samitiapp.api.samiti.auth.handler;

import com.samitiapp.api.samiti.auth.ctrl.InitiateLoginCtrl;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.samitiapp.api.samiti.common.SamitiApiResponse;

import java.util.concurrent.ExecutionException;


@RestController
@Log
public class InitiateLoginHandler {

    @Autowired
    private InitiateLoginCtrl ctrl;
    @PostMapping(path = "/v1/login/initiate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<?> initiateLogin(@RequestBody InitiateLoginCtrl.InitiateLoginRequest body) throws ExecutionException, InterruptedException {
        log.info("Initiate login start");
        SamitiApiResponse response = ctrl.run(body);
        log.info("Initiate login ends");
        if(response.errors != null) {
            return new ResponseEntity<>(response.errors, response.errors.getStatusCode());
        }
        return new ResponseEntity<>(response.success, response.successCode);
    }

}

