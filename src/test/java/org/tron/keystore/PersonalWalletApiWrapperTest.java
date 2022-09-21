
package org.tron.keystore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import javax.validation.constraints.AssertTrue;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.exception.CipherException;
import org.tron.walletcli.personal.PersonalWalletApiWrapper;

/**
 * 核心协议 https://tronprotocol.github.io/documentation-zh/mechanism-algorithm/system-contracts/
 */
@Slf4j
public class PersonalWalletApiWrapperTest {

  PersonalWalletApiWrapper personalWalletApiWrapper;

  @Before
  public void init() {
    this.personalWalletApiWrapper = new PersonalWalletApiWrapper();
  }

  /**
   * 1.创建账户 AccountCreateContract（支付了手续费）
   */
  @Test
  private void createAccount() {

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.createAccount(from, to);

    logger.info("createAccount result:{}", result);
    Assert.assertTrue(result);
  }


  /**
   * 2.转账（单位为 sun）
   */
  @Test
  public void sendCoin() {
    String from = "TYUMr6QQRFWy3bybuBRT2VV6rtUTkMNnKo";
    String to = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
    boolean result = personalWalletApiWrapper.sendCoin(from, to, 1000 * 1000 * 500L);

    logger.info("sendCoin result:{}", result);
  }

  /**
   * 3.TRC-10代币转账
   */
  @Test
  private void transferAsset() throws CipherException, IOException {
    String assetName = "1004964";//ByteArray.toHexString("01001101010101".getBytes());

    String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
    String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    boolean result = personalWalletApiWrapper.transferAsset(from, to, assetName, 1000L);

    logger.info("transferAsset result:{}", result);

  }

  /**
   * 4.投票超级节点
   */
  @Test
  private void voteWitness() {
    //Optional<GrpcAPI.ProposalList> proposalList = HanzhWalletApiWrapper.listProposals();

    //ByteString proposerAddress = proposalList.get().getProposals(0).getProposerAddress();

    //WalletApi.encode58Check(proposerAddress)

    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String to = "TPffmvjxEcvZefQqS7QYvL1Der3uiguikE";

    boolean result = personalWalletApiWrapper.voteWitness(from, to, 4L);

    logger.info("voteWitness result:{}", result);
  }

  /**
   * 5.创建超级节点候选人 WitnessCreateContract
   */
  @Test
  private void witnessCreate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url";
    boolean result = personalWalletApiWrapper.witnessCreate(from, url);

    logger.info("witnessCreate result:{}", result);
  }

  /**
   * 6.发布TRC-10代币 AssetIssueContract
   */
  @Test
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

  /**
   * 7.更新超级节点候选人URL WitnessUpdateContrac
   */
  @Test
  private void witnessUpdate() {
    String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
    String url = "url2";

    boolean result = personalWalletApiWrapper.witnessUpdate(from, url);

    logger.info("witnessUpdate result:{}", result);
  }

  /**
   * 8.购买代币
   */
  @Test
  private void participateAssetIssue() {
    String assetName = "1004964";

    try {
      String from = "TY7muqKzjTtpiGrXkvDGNF5EZkb5JYSijf";
      String to = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.participateAssetIssue(from, to, assetName, 1000);

      logger.info("participateAssetIssue result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  /**
   * 9.更新账户 AccountUpdateContract
   */
  @Test
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

  /**
   * 10.质押资产 FreezeBalanceContrac
   */
  @Test
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

  /**
   * 11.资产取消质押 UnfreezeBalanceContract
   */
  @Test
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

  /**
   * 12.提取奖励 WithdrawBalanceContract
   */
  @Test
  private void withdrawBalance() {
    try {

      String from = "TWvMa22K677paNS4CMvdvaJ3TqYZv2EG6o";
      boolean result = personalWalletApiWrapper.withdrawBalance(from);

      logger.info("withdrawBalance result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  /**
   * 13.解锁发布的Token UnfreezeAssetContract ??结算了什么
   */
  @Test
  private void unfreezeAsset() {
    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.unfreezeAsset(from);

      logger.info("unfreezeAsset result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  /**
   * 14.更新通证参数 UpdateAssetContract
   */
  @Test
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

  /**
   * 15.创建提议 ProposalCreateContract
   */
  @Test
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

  /**
   * 16.赞成提议 ProposalApproveContract
   */
  @Test
  private void proposalApprove() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalApprove(from, 1L, true);

      logger.info("proposalApprove result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }
  }

  /**
   * 17.删除提议 ProposalDeleteContract
   */
  @Test
  private void proposalDelete() {

    try {

      String from = "TXzNRYyYfHB2WmLe1JYYbL7kjzbN5FYiB7";
      boolean result = personalWalletApiWrapper.proposalDelete(from, 1L);

      logger.info("proposalDelete result:{}", result);
    } catch (Exception exception) {
      logger.error(exception.getMessage());
    }

  }

  /**
   * 18.设置账户ID SetAccountIdContract¶
   */
  @Test
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
