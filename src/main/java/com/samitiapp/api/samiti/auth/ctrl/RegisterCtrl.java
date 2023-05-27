package com.samitiapp.api.samiti.auth.ctrl;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.samitiapp.api.samiti.auth.models.PhoneMapping;
import com.samitiapp.api.samiti.auth.models.UserAccount;
import com.samitiapp.api.samiti.common.ErrorCodes;
import com.samitiapp.api.samiti.common.OTPGenerator;
import com.samitiapp.api.samiti.common.SamitiApiResponse;
import com.samitiapp.api.samiti.common.SamitiErrorResponse;
import io.grpc.StatusRuntimeException;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Component
public class RegisterCtrl {

    Firestore db;

    @Autowired
    public RegisterCtrl(Firestore db) {
        this.db = db;
    }

    private final Logger log = Logger.getLogger(RegisterCtrl.class.getName());

    public SamitiApiResponse run(RegisterRequest r) throws ExecutionException, InterruptedException {
        // TODO: check for permissions
        SamitiErrorResponse err = validate(r);
        if(null != err) {
            log.info("request body validation has failed");
            return new SamitiApiResponse(err);
        }

        return register(r);
    }


    private SamitiApiResponse register(RegisterRequest r) throws ExecutionException, InterruptedException {
        String Id = UUID.randomUUID().toString();
        PhoneMapping ph = r.toPhoneMapping(Id);
        UserAccount account = r.toAccount(Id);

        ph.setCreatedAt(System.currentTimeMillis());
        account.setCreatedAt(System.currentTimeMillis());
        account.setUpdatedAt(System.currentTimeMillis());

        String otp = OTPGenerator.generateOTP(4);
        long expiryTs = System.currentTimeMillis();
        expiryTs += 5 * 60 * 1000; // 5 minutes

        ph.setOtp(otp);
        ph.setOtpExpiresAt(expiryTs);

        try {
            ApiFuture<Void> res = db.runTransaction(transaction -> {
                DocumentReference phoneRef = db.collection(PhoneMapping.table).document(ph.getPhone());
                DocumentReference accountRef = db.collection(UserAccount.table).document(account.getId());

                transaction = transaction.create(phoneRef, ph);

                transaction.create(accountRef, account);
                return null;
            });

            res.get();
        } catch (Exception e) {
            if (ExceptionUtils.indexOfType(e, AlreadyExistsException.class) != -1) {
                return duplicateUserCreate();
            }
            // internal server error
            throw e;
        }

        RegisterResponse rr = new RegisterResponse("ok");

       return new SamitiApiResponse(rr);
    }


    private SamitiApiResponse duplicateUserCreate() {
        HashMap<String, String> errors = new HashMap<String, String>();
        errors.put("phone", "phone number already registered");
        SamitiErrorResponse err = SamitiErrorResponse.builder().build();
        err.setErrors(errors);
        err.setMessage("phone number already registered");
        err.setAppcode(1); // TODO:


        return new SamitiApiResponse(err);
    }

    private SamitiErrorResponse validate(RegisterRequest body) {
        log.info("request body validation start");

        HashMap<String, String> errors = body.validate();
        if(errors.size() != 0) {
            return SamitiErrorResponse.builder()
                    .errors(errors)
                    .appcode(ErrorCodes.INVALID_REQUEST_BODY)
                    .build();
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

        public UserAccount toAccount(String Id) {
            UserAccount account = new UserAccount(Id);
            account.setName(name);
            account.setPhone(phone);

            return account;

        }
        public PhoneMapping toPhoneMapping(String Id) {
            PhoneMapping ph = new PhoneMapping(phone);
            ph.setPhone(phone);
            ph.setId(Id);

            return ph;

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
