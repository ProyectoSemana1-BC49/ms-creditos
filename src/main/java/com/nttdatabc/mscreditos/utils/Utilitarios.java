package com.nttdatabc.mscreditos.utils;

import java.util.UUID;

/**
 * Utilitarios.
 */
public class Utilitarios {
  public static String generateUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

}
