package com.samitiapp.api.samiti.common;

import java.util.Random;

public class OTPGenerator {
    public static String generateOTP(int length) {
        String numbers="0123456789";
        Random random = new Random();
        char [] otp = new char[length];
        for(int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }
}
