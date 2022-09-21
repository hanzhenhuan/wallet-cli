package org.tron.walletcli.personal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.encoders.Hex;
import org.tron.common.utils.AbiUtil;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.exception.CipherException;

import java.io.IOException;
import java.util.HashMap;
import org.tron.protos.contract.SmartContractOuterClass.TriggerSmartContract;
import org.tron.walletserver.WalletApi;

/**
 * 核心协议 https://tronprotocol.github.io/documentation-zh/mechanism-algorithm/system-contracts/
 */
@Slf4j
public class PersonalClient {

  PersonalWalletApiWrapper personalWalletApiWrapper;

  public PersonalClient() {
    this.personalWalletApiWrapper = new PersonalWalletApiWrapper();
  }

  public static void main(String[] args) throws CipherException, IOException {
    PersonalClient client = new PersonalClient();

    String function = "participateAssetIssue";

    switch (function) {
      case "createAccount":
        // 1.创建账户 AccountCreateContract（支付了手续费）
        client.createAccount();
        break;
      case "sendCoin":
        // 2.转账（单位为 sun）
        client.sendCoin();
        break;
      case "transferAsset":
        // 3.TRC-10代币转账
        client.transferAsset();
        break;
      case "voteWitness":
        // 4.投票超级节点
        client.voteWitness();
        break;
      case "witnessCreate":
        // 5.创建超级节点候选人 WitnessCreateContract
        client.witnessCreate();
        break;
      case "assetIssue":
        // 6.发布TRC-10代币 AssetIssueContrac
        client.assetIssue();
        break;
      case "witnessUpdate":
        //7.更新超级节点候选人URL WitnessUpdateContrac
        client.witnessUpdate();
        break;
      case "participateAssetIssue":
        // 8.购买代币
        client.participateAssetIssue();
        break;
      case "accountUpdate":
        // 9.更新账户 AccountUpdateContract
        client.accountUpdate();
        break;
      case "freezeBalance":
        // 10.质押资产 FreezeBalanceContrac
        client.freezeBalance();
        break;
      case "unfreezeBalance":
        // 11.资产取消质押 UnfreezeBalanceContract
        client.unfreezeBalance();
        break;
      case "withdrawBalance":
        // 12.提取奖励 WithdrawBalanceContract
        client.withdrawBalance();
        break;
      case "unfreezeAsset":
        // 13.解锁发布的Token UnfreezeAssetContract ??结算了什么
        client.unfreezeAsset();
        break;
      case "updateAsset":
        //14.更新通证参数 UpdateAssetContract
        client.updateAsset();
        break;
      case "proposalCreate":
        // 15.创建提议 ProposalCreateContract
        client.proposalCreate();
        break;
      case "proposalApprove":
        //16.赞成提议 ProposalApproveContract
        client.proposalApprove();
        break;
      case "proposalDelete":
        // 17.删除提议 ProposalDeleteContract
        client.proposalDelete();
        break;
      case "setAccountId":
        // 18.设置账户ID SetAccountIdContract
        client.setAccountId();
        break;
      case "createSmartContract":
        // 19.创建智能合约 CreateSmartContract
        client.createSmartContract();
        break;
      case "triggerSmartContract":
        // 20.触发智能合约 TriggerSmartContract
        client.triggerSmartContract();
        break;
      case "updateSettingContract":
        // 21.更新合约 UpdateSettingContract
        client.updateSettingContract();
        break;
      case "accountPermissionUpdate":
        client.accountPermissionUpdate();
        break;
    }
  }


  private void createAccount() {

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.createAccount(from, to);

    logger.info("createAccount result:{}", result);
  }


  public void sendCoin() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.sendCoin(from, to, 1000 * 1000 * 20L);

    logger.info("sendCoin result:{}", result);
  }

  private void transferAsset() throws CipherException, IOException {
    String assetName = "1004964";//ByteArray.toHexString("01001101010101".getBytes());

    String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
    String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    boolean result = personalWalletApiWrapper.transferAsset(from, to, assetName, 1000L);

    logger.info("transferAsset result:{}", result);

  }


  private void voteWitness() {
    //Optional<GrpcAPI.ProposalList> proposalList = HanzhWalletApiWrapper.listProposals();

    //ByteString proposerAddress = proposalList.get().getProposals(0).getProposerAddress();

    //WalletApi.encode58Check(proposerAddress)

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TPffmvjxEcvZefQqS7QYvL1Der3uiguikE";

    boolean result = personalWalletApiWrapper.voteWitness(from, to, 4L);

    logger.info("voteWitness result:{}", result);
  }

  private void witnessCreate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url";
    boolean result = personalWalletApiWrapper.witnessCreate(from, url);

    logger.info("witnessCreate result:{}", result);
  }

  private void assetIssue() throws CipherException, IOException {

    String name = ByteArray.toHexString("01001101010101".getBytes());

    Date endDate = Utils.strToDateLong("2025-09-16");
    if (endDate == null) {
      logger.error("endDate format error");
      return;
    }
    logger.info("assetIssue name:{}", name);
    String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
    boolean assetIssue = personalWalletApiWrapper.createAssetIssue(from, name,
        ByteArray.toHexString("01001101010101".getBytes()),
        1000000L, 1,
        1, 6,
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

  private void witnessUpdate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url2";

    boolean result = personalWalletApiWrapper.witnessUpdate(from, url);

    logger.info("witnessUpdate result:{}", result);
  }

  private void participateAssetIssue() {
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

  private void accountUpdate() {
    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String name = "hanzh";
      boolean result = personalWalletApiWrapper.accountUpdate(from, name);

      logger.info("accountUpdate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void freezeBalance() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.freezeBalance(from, 50 * 1000 * 1000, 3,
          1, to);

      result = personalWalletApiWrapper.freezeBalance(from, 50 * 1000 * 1000, 3,
          0, to);

      logger.info("freezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  private void unfreezeBalance() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
      boolean result = personalWalletApiWrapper.unfreezeBalance(from, 1, to);

      logger.info("unfreezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void withdrawBalance() {
    try {

      String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
      boolean result = personalWalletApiWrapper.withdrawBalance(from);

      logger.info("withdrawBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void unfreezeAsset() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.unfreezeAsset(from);

      logger.info("unfreezeAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void updateAsset() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.updateAsset(from, "描述", "url3",
          1000, 1000);

      logger.info("updateAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }


  private void proposalCreate() {
    try {

      // https://cn.developers.tron.network/docs/super-representatives#tron%E7%BD%91%E7%BB%9C%E5%8F%82%E6%95%B0

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalCreate(from, 1L, 8888L);

      logger.info("proposalCreate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void proposalApprove() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalApprove(from, 1L, true);

      logger.info("proposalApprove result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void proposalDelete() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalDelete(from, 1L);

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  private void setAccountId() {
    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.setAccountId(from, "21223123123");

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }


  public void createSmartContract() throws IOException {
    URL resource = PersonalClient.class.getResource("/");
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
    String constructorStr = "";
    String argsStr = "";
    long feeLimit = 100 * 1000 * 1000L;
    long consumeUserResourcePercent = 100L;
    long originEnergyLimit = 1000 * 1000L;
    long value = 10;
    long tokenValue = 0;
    String tokenId = "";
    String libraryAddressPair = null;
    String compilerVersion = null;
    try {

      boolean result = personalWalletApiWrapper.deployContract(from, contractName, abiStr, codeStr,
          feeLimit, value, consumeUserResourcePercent, originEnergyLimit, tokenValue, tokenId,
          libraryAddressPair, compilerVersion);

      logger.info("deployContract result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

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
    boolean isConstant = false;

    try {

      boolean result = personalWalletApiWrapper.callContract(from, contractAddress, callValue,
          input, feeLimit, tokenValue, tokenId, isConstant);

      logger.info("callContract result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void updateSettingContract() {

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

  private void accountPermissionUpdate() {
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
    list.forEach(e -> {
      operations[e / 8] |= (1 << e % 8);
    });
    return ByteArray.toHexString(operations);
  }


}
