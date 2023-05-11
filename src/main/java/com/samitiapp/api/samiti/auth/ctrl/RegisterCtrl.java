package com.samitiapp.api.samiti.auth.ctrl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.samitiapp.api.samiti.auth.models.UserAccount;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import com.samitiapp.api.samiti.db.DbProvider;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

@Component
public class RegisterCtrl {

    @Autowired
    Firestore db;

    private final Logger log = Logger.getLogger(RegisterCtrl.class.getName());

    public SamitiApiResponse run(RegisterRequest r) throws IOException {
        // TODO: check for permissions
        SamitiErrorResponse err = validate(r);
        if(null != err) {
            log.info("request body validation has failed");
            return new SamitiApiResponse(err);
        }

        return register(r);

    }


    private SamitiApiResponse register(RegisterRequest r) throws IOException {
        UserAccount account = r.toAccount();

        ApiFuture<WriteResult> result = db.collection(UserAccount.table)
                .document(account.getId())
                .set(account);

        RegisterResponse rr = new RegisterResponse("ok");

       return new SamitiApiResponse(rr);
    }

    private SamitiErrorResponse validate(RegisterRequest body) {
        log.info("request body validation start");

        HashMap<String, String> errors = body.validate();
        if(errors.size() != 0) {
            SamitiErrorResponse err = new SamitiErrorResponse();
            err.setErrors(errors);
            err.setAppcode(1);
            return err;
        }

        log.info("request body validation successful");


        // everything okay
        return null;
    }




    //@Data
    public static class RegisterResponse {
        public String status;

        public RegisterResponse(String ok) {
            status = ok;
        }
    }

    @Data
    public static class RegisterRequest {
        public String phone;
        public String name;

        public UserAccount toAccount() {
            UserAccount account = new UserAccount();
            account.setName(name);
            account.setPhone(phone);

            return account;

        }

        public HashMap<String, String> validate() {
            HashMap<String, String> errors = new HashMap<>();

            if (phone.length() != 10) {
                errors.put("phone", "not valid phone");
            }
            // TODO: advanced check is required

            return errors ;
        }
    }


}
