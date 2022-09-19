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
public class HanzhClient {

  HanzhWalletApiWrapper walletApiWrapper;

  public HanzhClient() {
    this.walletApiWrapper = new HanzhWalletApiWrapper();
  }

  public static void main(String[] args) throws CipherException, IOException {
    HanzhClient client = new HanzhClient();
    // 1.创建账户 AccountCreateContract（支付了手续费）
    client.createAccount();

    // 2.转账（单位为 sun）
    client.sendCoin();

    // 3.TRC-10代币转账
    client.transferAsset();

    // 4.投票超级节点
    client.voteWitness();

    // 5.创建超级节点候选人 WitnessCreateContract
    client.witnessCreate();

    // 6.发布TRC-10代币 AssetIssueContrac
    client.assetIssue();

    //7.更新超级节点候选人URL WitnessUpdateContrac
    client.witnessUpdate();

    // 8.购买代币
    client.participateAssetIssue();

    // 9.更新账户 AccountUpdateContract
    client.accountUpdate();

    // 10.质押资产 FreezeBalanceContrac
    client.freezeBalance();

    // 11.资产取消质押 UnfreezeBalanceContract
    client.unfreezeBalance();

    // 12.提取奖励 WithdrawBalanceContract
    client.withdrawBalance();

    // 13.解锁发布的Token UnfreezeAssetContract ??结算了什么
    client.unfreezeAsset();

    //14.更新通证参数 UpdateAssetContract
    client.updateAsset();

    // 15.创建提议 ProposalCreateContract
    client.proposalCreate();

    //16.赞成提议 ProposalApproveContract
    client.proposalApprove();

    // 17.删除提议 ProposalDeleteContract
    client.proposalDelete();

    // 18.设置账户ID SetAccountIdContract¶
    client.setAccountId();
  }

  private void createAccount() {

    boolean result = walletApiWrapper
        .createAccount("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o"
        );

    logger.info("createAccount result:{}", result);
  }


  public void sendCoin() {
    boolean result = walletApiWrapper
        .sendCoin("TYUMr6QQRFWy3bybuBRT2VV6rtUTkMNnKo", "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o",
            1000 * 1000 * 500L);

    logger.info("sendCoin result:{}", result);
  }

  private void transferAsset() throws CipherException, IOException {
    String assetName = "1004964";//ByteArray.toHexString("01001101010101".getBytes());

    boolean result = walletApiWrapper
        .transferAsset("TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf", "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7",
            assetName, 1000L);

    logger.info("transferAsset result:{}", result);

  }


  private void voteWitness() {
    //Optional<GrpcAPI.ProposalList> proposalList = HanzhWalletApiWrapper.listProposals();

    //ByteString proposerAddress = proposalList.get().getProposals(0).getProposerAddress();

    //WalletApi.encode58Check(proposerAddress)

    String to = "TPffmvjxEcvZefQqS7QYvL1Der3uiguikE";

    boolean result = walletApiWrapper.voteWitness("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7",
        to, 4L);

    logger.info("voteWitness result:{}", result);
  }

  private void witnessCreate() {
    boolean result = walletApiWrapper.witnessCreate("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "url");

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
    boolean assetIssue = walletApiWrapper.createAssetIssue("TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf",
        name,
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
    boolean result = walletApiWrapper.witnessUpdate("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "url2");

    logger.info("witnessUpdate result:{}", result);
  }

  private void participateAssetIssue() {
    String assetName = "1004964";

    try {
      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      String to = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
      boolean result = walletApiWrapper.participateAssetIssue(from, to, assetName, 1000);

      logger.info("participateAssetIssue result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void accountUpdate() {
    try {
      boolean result = walletApiWrapper
          .accountUpdate("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "hanzh");

      logger.info("accountUpdate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void freezeBalance() {
    try {

      boolean result = walletApiWrapper
          .freezeBalance("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", 2000 * 1000, 3, 1,
              "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7");

      logger.info("freezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  private void unfreezeBalance() {

    try {

      boolean result = walletApiWrapper.unfreezeBalance("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", 1,
          "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf");

      logger.info("unfreezeBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void withdrawBalance() {
    try {

      boolean result = walletApiWrapper.withdrawBalance("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7");

      logger.info("withdrawBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void unfreezeAsset() {
    try {

      boolean result = walletApiWrapper.unfreezeAsset("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7");

      logger.info("unfreezeAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void updateAsset() {
    try {

      boolean result = walletApiWrapper
          .updateAsset("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "描述", "url3", 1000, 1000);

      logger.info("updateAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }


  private void proposalCreate() {
    try {

      // https://cn.developers.tron.network/docs/super-representatives#tron%E7%BD%91%E7%BB%9C%E5%8F%82%E6%95%B0

      boolean result = walletApiWrapper
          .proposalCreate("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", 1L, 8888L);

      logger.info("proposalCreate result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void proposalApprove() {

    try {

      boolean result = walletApiWrapper
          .proposalApprove("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", 1L, true);

      logger.info("proposalApprove result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  private void proposalDelete() {

    try {

      boolean result = walletApiWrapper.proposalDelete("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", 1L);

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  private void setAccountId() {
    try {
      boolean result = walletApiWrapper
          .setAccountId("TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7", "21223123123");

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }


}
