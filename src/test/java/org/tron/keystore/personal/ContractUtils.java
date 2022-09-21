package org.tron.keystore.personal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.tron.common.utils.ByteArray;

public class ContractUtils {


  public static String getOperations(Integer[] contractId) {
    List<Integer> list = new ArrayList<>(Arrays.asList(contractId));
    byte[] operations = new byte[32];
    list.forEach(e -> operations[e / 8] |= (1 << e % 8));
    return ByteArray.toHexString(operations);
  }

}
