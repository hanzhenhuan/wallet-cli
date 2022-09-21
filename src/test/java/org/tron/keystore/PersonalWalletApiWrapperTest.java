package org.tron.keystore;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.tron.common.utils.AbiUtil;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.exception.CipherException;
import org.tron.walletcli.personal.PersonalWalletApiWrapper;

/**
 * core protocol https://tronprotocol.github.io/documentation-zh/mechanism-algorithm/system-contracts/
 */
@Slf4j
public class PersonalWalletApiWrapperTest {

  PersonalWalletApiWrapper personalWalletApiWrapper;

  public PersonalWalletApiWrapperTest() {
    this.personalWalletApiWrapper = new PersonalWalletApiWrapper();
  }


  @Test
  public void createAccount() {

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.createAccount(from, to);

    logger.info("createAccount result:{}", result);
  }

  @Test
  public void sendCoin() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.sendCoin(from, to, 1000 * 1000 * 20L);

    logger.info("sendCoin result:{}", result);
  }

  @Test
  public void transferAsset() throws CipherException, IOException {
    String assetName = "1004964";//ByteArray.toHexString("01001101010101".getBytes());

    String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
    String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    boolean result = personalWalletApiWrapper.transferAsset(from, to, assetName, 1000L);

    logger.info("transferAsset result:{}", result);

  }

  @Test
  public void voteWitness() {
    //Optional<GrpcAPI.ProposalList> proposalList = HanzhWalletApiWrapper.listProposals();

    //ByteString proposerAddress = proposalList.get().getProposals(0).getProposerAddress();

    //WalletApi.encode58Check(proposerAddress)

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TPffmvjxEcvZefQqS7QYvL1Der3uiguikE";

    boolean result = personalWalletApiWrapper.voteWitness(from, to, 4L);

    logger.info("voteWitness result:{}", result);
  }

  @Test
  public void witnessCreate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url";
    boolean result = personalWalletApiWrapper.witnessCreate(from, url);

    logger.info("witnessCreate result:{}", result);
  }

  @Test
  public void assetIssue() throws CipherException, IOException {
    String name = ByteArray.toHexString("测试字符串".getBytes());
    String abbrName = ByteArray.toHexString("测试".getBytes());

    Date endDate = Utils.strToDateLong("2025-09-16");
    if (endDate == null) {
      logger.error("endDate format error");
      return;
    }
    logger.info("assetIssue name:{}", name);
    String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean assetIssue = personalWalletApiWrapper.createAssetIssue(from, name,
        abbrName, 1000000L, 1, 1, 6,
        System.currentTimeMillis() + (1000 * 60),
        endDate.getTime(),
        10000000L, 1000000L, 0,
        "测试", "测试", new HashMap<String, String>() {
          {
            put("1000", "2");
            put("2000", "3");
          }
        }
    );

    logger.info("createAssetIssue result:{}", assetIssue);
  }

  @Test
  public void witnessUpdate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url2";

    boolean result = personalWalletApiWrapper.witnessUpdate(from, url);

    logger.info("witnessUpdate result:{}", result);
  }

  @Test
  public void participateAssetIssue() {
    String assetName = "1004963";

    try {
      String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
      String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.participateAssetIssue(from, to, assetName, 1000);

      logger.info("participateAssetIssue result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void accountUpdate() {
    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String name = "hanzh";
      boolean result = personalWalletApiWrapper.accountUpdate(from, name);

      logger.info("accountUpdate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void freezeBalance() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.freezeBalance(from, 50 * 1000 * 1000, 3,
          1, to);

      logger.info("freezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  @Test
  public void unfreezeBalance() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
      boolean result = personalWalletApiWrapper.unfreezeBalance(from, 1, to);

      logger.info("unfreezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void withdrawBalance() {
    try {

      String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
      boolean result = personalWalletApiWrapper.withdrawBalance(from);

      logger.info("withdrawBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void unfreezeAsset() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.unfreezeAsset(from);

      logger.info("unfreezeAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void updateAsset() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.updateAsset(from, "描述", "url3",
          1000, 1000);

      logger.info("updateAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void proposalCreate() {
    try {

      // https://cn.developers.tron.network/docs/super-representatives#tron%E7%BD%91%E7%BB%9C%E5%8F%82%E6%95%B0

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalCreate(from, 1L, 8888L);

      logger.info("proposalCreate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void proposalApprove() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalApprove(from, 1L, true);

      logger.info("proposalApprove result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void proposalDelete() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalDelete(from, 1L);

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  @Test
  public void setAccountId() {
    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.setAccountId(from, "21223123123");

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void createSmartContract() throws IOException {
    URL resource = PersonalWalletApiWrapperTest.class.getResource("/");
    if (resource == null) {
      return;
    }

    String classPath = resource.getPath();
    String abiPath = classPath + "PersonalSmartContract.abi";
    String binPath = classPath + "PersonalSmartContract.bin";

    String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    String contractName = "PersonalContract2";
    String abiStr = FileUtils.readFileToString(new File(abiPath), "utf-8");
    String codeStr = FileUtils.readFileToString(new File(binPath), "utf-8");

    long feeLimit = 100 * 1000 * 1000L;
    long consumeUserResourcePercent = 100L;
    long originEnergyLimit = 1000 * 1000L;
    long value = 10;
    long tokenValue = 0;
    String tokenId = "";
    try {

      boolean result = personalWalletApiWrapper.deployContract(from, contractName, abiStr, codeStr,
          feeLimit, value, consumeUserResourcePercent, originEnergyLimit, tokenValue, tokenId,
          null, null);

      logger.info("deployContract result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void triggerSmartContract() {

    String contractAddress = "TYRxYxQwAcbBiJhrXEV3gxh6Qeby2u7wNn";

    String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
    String methodStr = "setA(uint256)";
    String argsStr = "500";
    byte[] input = Hex.decode(AbiUtil.parseMethod(methodStr, argsStr, false));
    long feeLimit = 100 * 1000 * 1000;
    long callValue = 0;
    long tokenValue = 0;
    String tokenId = "";

    try {

      boolean result = personalWalletApiWrapper.callContract(from, contractAddress, callValue,
          input, feeLimit, tokenValue, tokenId, false);

      logger.info("callContract result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void updateSettingContract() {

    String contractAddress = "TYRxYxQwAcbBiJhrXEV3gxh6Qeby2u7wNn";

    String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";

    long consumeUserResourcePercent = 90L;
    try {

      boolean result = personalWalletApiWrapper.updateSettingContract(from, contractAddress,
          consumeUserResourcePercent);

      logger.info("callContract result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  @Test
  public void accountPermissionUpdate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";

    JsonObject key1 = new JsonObject();
    key1.addProperty("address", "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o");
    key1.addProperty("weight", 1);

    JsonObject key2 = new JsonObject();
    key2.addProperty("address", "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf");
    key2.addProperty("weight", 1);

    JsonArray keys = new JsonArray();
    keys.add(key1);
    keys.add(key2);

    JsonObject permissionJsonObj = new JsonObject();

    JsonObject owner = new JsonObject();
    owner.addProperty("type", 0);
    owner.addProperty("permission_name", "owner");
    owner.addProperty("threshold", 2);
    owner.addProperty("parent_id", 0);
    owner.add("keys", keys);

    JsonArray witnessKeys = new JsonArray();
    witnessKeys.add(key1);
    JsonObject witness = new Gson().fromJson(owner.toString(), JsonObject.class);
    witness.addProperty("type", 1);
    witness.addProperty("threshold", 1);
    witness.addProperty("permission_name", "witness");
    witness.add("keys", witnessKeys);

    String operations = getOperations(new Integer[]{0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12});

    JsonObject active = new Gson().fromJson(owner.toString(), JsonObject.class);
    active.addProperty("type", 2);
    active.addProperty("permission_name", "active0");
    active.addProperty("operations", operations);
    active.add("keys", keys);

    JsonArray actives = new JsonArray();
    actives.add(active);

    permissionJsonObj.add("owner_permission", owner);
    permissionJsonObj.add("witness_permission", witness);
    permissionJsonObj.add("active_permissions", actives);

    String permissionJson = permissionJsonObj.toString();

    try {

      boolean result = personalWalletApiWrapper.accountPermissionUpdate(from, permissionJson);

      logger.info("accountPermissionUpdate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  public static String getOperations(Integer[] contractId) {
    List<Integer> list = new ArrayList<>(Arrays.asList(contractId));
    byte[] operations = new byte[32];
    list.forEach(e -> operations[e / 8] |= (1 << e % 8));
    return ByteArray.toHexString(operations);
  }


}
