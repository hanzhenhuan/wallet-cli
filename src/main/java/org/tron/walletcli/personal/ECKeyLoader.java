package org.tron.walletcli.personal;

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

  static {

    try {
      List<String> address2PrivateKeys = FileUtils.readLines(
          new File("/Users/hanzhenhuan/Desktop/tron_account"), "utf-8");

      for (String address2PrivateKey : address2PrivateKeys) {
        String[] pair = address2PrivateKey.split(",");
        byte[] db = ByteArray.fromHexString(pair[1]);

        address2ECKeyMap.put(pair[0], new ECKey(db, true));
      }


    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  public static ECKey getECKeyByAddress(String address) {
    ECKey privateKey = address2ECKeyMap.get(address);
    if (privateKey == null) {
      throw new RuntimeException("不支持的address");
    }
    return privateKey;
  }


}
