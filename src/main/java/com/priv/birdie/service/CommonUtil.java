package com.priv.birdie.service;

import java.util.UUID;

public class CommonUtil {

    public static String generateUuid(){
        return UUID.randomUUID().toString();
    }

}
