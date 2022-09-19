package org.tron.walletcli.personal;

import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;

import java.util.HashMap;
import java.util.Map;

public class ECKeyLoader {


  public static Map<String, String> address2PrivateKeyMap = new HashMap<String, String>() {{
    put("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7",
        "970828656066c0134decd2c56435b29889621c1a41ec9824a6a1697514cb4dec");
    put("TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf",
        "ad31e66b04f54a3414e2e5d55a6d50a424d3ea2a1e3458108f8ee855d9da04f7");
    put("TYUMr6QQRFWy3bybuBRT2VV6rtUTkMNnKo",
        "c5bf367a1a3e5a01cf14ae76f843eeb7269e7243707cff3ed95afdebe546ad6a");
  }};


  public static Map<String, ECKey> address2ECKeyMap = new HashMap<>();

  static {

    for (Map.Entry<String, String> entry : address2PrivateKeyMap.entrySet()) {
      byte[] db = ByteArray.fromHexString(entry.getValue());

      address2ECKeyMap.put(entry.getKey(), new ECKey(db, true));
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
