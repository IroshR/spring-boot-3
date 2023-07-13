package com.iroshnk.nftraffle.config.bean;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence plainTextPassword) {
        //return rawPassword.toString();
        return BCrypt.hashpw(plainTextPassword.toString(), BCrypt.gensalt(8));
    }

    @Override
    public boolean matches(CharSequence plainTextPassword, String encodedPassword) {
        //return true;
        System.out.println("-----------------------------------------------");
        long s = System.currentTimeMillis();
        boolean sa = BCrypt.checkpw(plainTextPassword.toString(), encodedPassword);
        long e = System.currentTimeMillis();
        System.out.println(e - s);
        return sa;
    }
}
