package com.samitiapp.api.samiti.auth.handler;

import com.samitiapp.api.samiti.auth.ctrl.RegisterCtrl;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Logger;


@RestController
public class RegisterHandler {

    private final Logger log = Logger.getLogger(RegisterHandler.class.getName());

    @Autowired
    private RegisterCtrl ctrl;

    @PostMapping(path = "/v1/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterCtrl.RegisterRequest body) throws IOException {
        log.info("registration request start");

        SamitiApiResponse response = ctrl.run(body);

        log.info("registration request ends");
        return new ResponseEntity<>(response.success, response.successCode);
    }

}
