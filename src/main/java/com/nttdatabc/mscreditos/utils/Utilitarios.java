package com.nttdatabc.mscreditos.utils;

import java.util.UUID;

public class Utilitarios {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
