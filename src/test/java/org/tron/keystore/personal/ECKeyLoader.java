package org.tron.keystore.personal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;

public class ECKeyLoader {


  public static Map<String, ECKey> address2ECKeyMap = new HashMap<>();
  public static Map<String, byte[]> address2PrivateKeyMap = new HashMap<>();

  static {

    try {
      List<String> address2PrivateKeys = FileUtils.readLines(
          new File("/Users/hanzhenhuan/Desktop/tron_account"), "utf-8");

      for (String address2PrivateKey : address2PrivateKeys) {
        String[] pair = address2PrivateKey.split(",");
        byte[] db = ByteArray.fromHexString(pair[1]);

        address2ECKeyMap.put(pair[0], new ECKey(db, true));
        address2PrivateKeyMap.put(pair[0], db);
      }


    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  public static ECKey getECKeyByAddress(String address) {
    ECKey privateKey = address2ECKeyMap.get(address);
    if (privateKey == null) {
      throw new RuntimeException("unsupported address");
    }
    return privateKey;
  }

  public static byte[] getPrivateKey(String address) {
    byte[] privateKey = address2PrivateKeyMap.get(address);
    if (privateKey == null) {
      throw new RuntimeException("unsupported address");
    }
    return privateKey;
  }


}
