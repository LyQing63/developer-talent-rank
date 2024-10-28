package com.talent.utils;

public class TokenUtils {

    public static String getToken(String authorization) {
        String[] s = authorization.split(" ");

        if (s.length < 2) {
            return null;
        }

        return s[1];
    }
}
