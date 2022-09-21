
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


}
