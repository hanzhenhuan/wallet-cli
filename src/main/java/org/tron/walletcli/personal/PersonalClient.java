package org.tron.walletcli.personal;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.exception.CipherException;

import java.io.IOException;
import java.util.HashMap;

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

    String function = "sendCoin";
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
        // 18.设置账户ID SetAccountIdContract¶
        client.setAccountId();
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
    String from = "TYUMr6QQRFWy3bybuBRT2VV6rtUTkMNnKo";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.sendCoin(from, to, 1000 * 1000 * 500L);

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
    String assetName = "1004964";

    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
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
      boolean result = personalWalletApiWrapper.freezeBalance(from, 2000 * 1000, 3,
          1, to);

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


}
