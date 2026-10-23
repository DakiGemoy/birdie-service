package com.priv.birdie.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class Constant {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RespCode{
        public static String SUCCESS = "00";
        public static String DATA_NOT_FOUND = "01";
        public static String ACCOUNT_BLOCKED = "02";
        public static String ACCOUNT_INACTIVE = "03";
        public static String BAD_REQUEST = "04";
        public static String BAD_CREDENTIAL = "05";
        public static String ERROR_DATABASE = "06";
        public static String INTERNAL_ERROR = "99";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Role{
        public static String ADMIN = "ROLE_ADMIN";
        public static String USER = "ROLE_USER";
    }

}
