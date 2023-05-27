package com.samitiapp.api.samiti.samiti.handlers;

import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.samiti.ctrl.CreateSamitiCtrl;
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
public class CreateSamitiHandler {

    @Autowired
    private CreateSamitiCtrl ctrl;

    @PostMapping(path = "/v1/create",
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody CreateSamitiCtrl.CreateSamitiRequest body,
                                    @RequestHeader(required = true, name = "Authorization") String token) throws ExecutionException, InterruptedException {
        log.info("create samiti start");

        SamitiApiResponse response = ctrl.run(body, token);

        log.info("create samiti ends");

        if (response.errors != null) {
            return new ResponseEntity<>(response.errors, response.errors.getStatusCode());
        }

        return new ResponseEntity<>(response.success, response.successCode);

    }

}
