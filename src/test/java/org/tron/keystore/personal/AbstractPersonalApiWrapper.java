package org.tron.keystore.personal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.typesafe.config.Config;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.TransactionExtention;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Sm3Hash;
import org.tron.common.utils.Base58;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.common.utils.Utils;
import org.tron.core.config.Configuration;
import org.tron.core.config.Parameter;
import org.tron.core.exception.CancelException;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Key;
import org.tron.protos.Protocol.Permission;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Result;
import org.tron.protos.Protocol.TransactionSign;
import org.tron.protos.contract.AssetIssueContractOuterClass.ParticipateAssetIssueContract;
import org.tron.protos.contract.AssetIssueContractOuterClass.TransferAssetContract;
import org.tron.walletserver.GrpcClient;
import org.tron.walletserver.WalletApi;

@Slf4j
public abstract class AbstractPersonalApiWrapper {

  public static final GrpcClient rpcCli = init();


  public static GrpcClient init() {
    Config config = Configuration.getByPath("config.conf");

    String fullNode = "";
    String solidityNode = "";
    if (config.hasPath("soliditynode.ip.list")) {
      solidityNode = config.getStringList("soliditynode.ip.list").get(0);
    }
    if (config.hasPath("fullnode.ip.list")) {
      fullNode = config.getStringList("fullnode.ip.list").get(0);
    }
    if (config.hasPath("net.type") && "mainnet".equalsIgnoreCase(config.getString("net.type"))) {
      WalletApi.setAddressPreFixByte(Parameter.CommonConstant.ADD_PRE_FIX_BYTE_MAINNET);
    } else {
      WalletApi.setAddressPreFixByte(Parameter.CommonConstant.ADD_PRE_FIX_BYTE_TESTNET);
    }

    return new GrpcClient(fullNode, solidityNode);
  }

  public boolean processTransactionExtention(String address,
      GrpcAPI.TransactionExtention extention) {

    Protocol.Transaction transaction = extention.getTransaction();
    if (transaction.getRawData().getContractCount() < 1) {
      return false;
    }

    // 匿名交易合同，该类型已经不支持
    if (transaction.getRawData().getContract(0).getType()
        == Protocol.Transaction.Contract.ContractType.ShieldedTransferContract) {
      return false;
    }

    try {
      if (address.equals("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7")) {
        byte[] pk1 = ECKeyLoader.getPrivateKey("TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o");
        byte[] pk2 = ECKeyLoader.getPrivateKey("TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf");
        TransactionExtention ext = addSignByApi(transaction, pk1, 0);
        ext = WalletApi.addSignByApi(ext.getTransaction(), pk2);
        transaction = ext.getTransaction();
      } else {
        transaction = signTransaction(address, transaction);
      }
    } catch (Exception exception) {
      throw new RuntimeException("signTransaction failed");
    }

    if (rpcCli.broadcastTransaction(transaction)) {

      // 记录交易id
      logger.info("broadcastTransaction hash:{}", getTransactionHash(transaction));
      return true;
    }
    return false;
  }


  public TransactionExtention supplyTransactionLimit(TransactionExtention tnxExt, long feeLimit) {

    TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = tnxExt.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < tnxExt.getTransaction().getSignatureCount(); i++) {
      ByteString s = tnxExt.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < tnxExt.getTransaction().getRetCount(); i++) {
      Result r = tnxExt.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    texBuilder.setTransaction(transBuilder);
    texBuilder.setResult(tnxExt.getResult());
    texBuilder.setTxid(tnxExt.getTxid());
    return texBuilder.build();
  }


  public Protocol.Transaction signTransaction(String address, Protocol.Transaction transaction) {

    ECKey ecKey = ECKeyLoader.getECKeyByAddress(address);

    Protocol.Transaction sign = TransactionUtils.sign(transaction, ecKey);

    GrpcAPI.TransactionSignWeight weight = rpcCli.getTransactionSignWeight(sign);
    if (weight.getResult().getCode()
        == GrpcAPI.TransactionSignWeight.Result.response_code.ENOUGH_PERMISSION) {
      return sign;
    }
    if (weight.getResult().getCode()
        == GrpcAPI.TransactionSignWeight.Result.response_code.NOT_ENOUGH_PERMISSION) {
      System.out.println("Current signWeight is:");
      System.out.println(Utils.printTransactionSignWeight(weight));
      System.out.println("Please confirm if continue add signature enter y or Y, else any other");

      throw new RuntimeException("no permission");
    }

    return sign;

  }


  public static TransactionExtention addSignByApi(Transaction transaction, byte[] privateKey,
      int permissionId) throws CancelException {

    transaction = TransactionUtils.setExpirationTime(transaction);
    String tipsString = "Please input permission id.";
    transaction = setPermissionId(transaction, tipsString, permissionId);
    TransactionSign.Builder builder = TransactionSign.newBuilder();
    builder.setPrivateKey(ByteString.copyFrom(privateKey));
    builder.setTransaction(transaction);
    return rpcCli.addSign(builder.build());
  }

  public static String getTransactionHash(Transaction transaction) {
    return ByteArray.toHexString(Sha256Sm3Hash.hash(transaction.getRawData().toByteArray()));
  }


  public ParticipateAssetIssueContract createParticipateAssetIssueContract(byte[] to,
      byte[] assertName, byte[] owner, long amount) {

    ParticipateAssetIssueContract.Builder builder = ParticipateAssetIssueContract.newBuilder();

    builder.setToAddress(ByteString.copyFrom(to));
    builder.setAssetName(ByteString.copyFrom(assertName));
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    builder.setAmount(amount);

    return builder.build();
  }


  public static TransferAssetContract createTransferAssetContract(byte[] to, byte[] assertName,
      byte[] owner, long amount) {

    TransferAssetContract.Builder builder = TransferAssetContract.newBuilder();

    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    return builder.build();
  }


  public static Permission json2Permission(JSONObject json) {
    Permission.Builder permissionBuilder = Permission.newBuilder();
    if (json.containsKey("type")) {
      int type = json.getInteger("type");
      permissionBuilder.setTypeValue(type);
    }
    if (json.containsKey("permission_name")) {
      String permission_name = json.getString("permission_name");
      permissionBuilder.setPermissionName(permission_name);
    }
    if (json.containsKey("threshold")) {
      long threshold = json.getLong("threshold");
      permissionBuilder.setThreshold(threshold);
    }
    if (json.containsKey("parent_id")) {
      int parent_id = json.getInteger("parent_id");
      permissionBuilder.setParentId(parent_id);
    }
    if (json.containsKey("operations")) {
      byte[] operations = ByteArray.fromHexString(json.getString("operations"));
      permissionBuilder.setOperations(ByteString.copyFrom(operations));
    }
    if (json.containsKey("keys")) {
      JSONArray keys = json.getJSONArray("keys");
      List<Key> keyList = new ArrayList<>();
      for (int i = 0; i < keys.size(); i++) {
        Key.Builder keyBuilder = Key.newBuilder();
        JSONObject key = keys.getJSONObject(i);
        String address = key.getString("address");
        long weight = key.getLong("weight");
        byte[] addressBytes = decode58Check(address);
        if (addressBytes == null) {
          throw new RuntimeException("decode58Check from address failed");
        }
        keyBuilder.setAddress(ByteString.copyFrom(addressBytes));
        keyBuilder.setWeight(weight);
        keyList.add(keyBuilder.build());
      }
      permissionBuilder.addAllKeys(keyList);
    }
    return permissionBuilder.build();
  }


  public static byte[] decode58Check(String input) {
    byte[] decodeCheck = Base58.decode(input);
    if (decodeCheck.length <= 4) {
      return null;
    }
    byte[] decodeData = new byte[decodeCheck.length - 4];
    System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
    byte[] hash0 = Sha256Sm3Hash.hash(decodeData);
    byte[] hash1 = Sha256Sm3Hash.hash(hash0);
    if (hash1[0] == decodeCheck[decodeData.length]
        && hash1[1] == decodeCheck[decodeData.length + 1]
        && hash1[2] == decodeCheck[decodeData.length + 2]
        && hash1[3] == decodeCheck[decodeData.length + 3]) {
      return decodeData;
    }
    return null;
  }


  public static Transaction setPermissionId(Transaction transaction, String tipString,
      int permissionId) throws CancelException {

    if (transaction.getSignatureCount() != 0
        || transaction.getRawData().getContract(0).getPermissionId() != 0) {
      return transaction;
    }

    System.out.println(tipString);
    if (permissionId < 0) {
      throw new CancelException("User cancelled");
    }
    if (permissionId != 0) {
      Transaction.raw.Builder raw = transaction.getRawData().toBuilder();
      Transaction.Contract.Builder contract =
          raw.getContract(0).toBuilder().setPermissionId(permissionId);
      raw.clearContract();
      raw.addContract(contract);
      transaction = transaction.toBuilder().setRawData(raw).build();
    }
    return transaction;
  }
}
