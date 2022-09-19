package org.tron.walletcli.personal;

import com.google.protobuf.ByteString;
import com.typesafe.config.Config;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Sm3Hash;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.common.utils.Utils;
import org.tron.core.config.Configuration;
import org.tron.core.config.Parameter;
import org.tron.core.exception.CipherException;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.contract.AccountContract;
import org.tron.protos.contract.AssetIssueContractOuterClass;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.ProposalContract;
import org.tron.protos.contract.WitnessContract;
import org.tron.walletserver.GrpcClient;
import org.tron.walletserver.WalletApi;

@Slf4j
public class HanzhWalletApiWrapper {

  private static GrpcClient rpcCli = init();

  private static int rpcVersion = 0;

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
    if (config.hasPath("RPC_version")) {
      rpcVersion = config.getInt("RPC_version");
      System.out.println("WalletApi getRpcVsersion: " + rpcVersion);
    }

    return new GrpcClient(fullNode, solidityNode);
  }

  public HanzhWalletApiWrapper() {
    WalletApi.init();
  }


  public boolean createAccount(String from, String to, int type) {
    byte[] ownerAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    AccountContract.AccountCreateContract.Builder builder = AccountContract.AccountCreateContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(ownerAddress));
    builder.setAccountAddress(ByteString.copyFrom(toAddress));

    GrpcAPI.TransactionExtention extention = rpcCli.createAccount2(builder.build());
    if (extention == null || !extention.getResult().getResult()) {
      logger.info("createAccount extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  /**
   * 转账TRX
   *
   * @param from   address
   * @param to     address
   * @param amount sun
   */
  public boolean sendCoin(String from, String to, Long amount) throws CipherException, IOException {
    byte[] ownerAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    BalanceContract.TransferContract.Builder builder = BalanceContract.TransferContract
        .newBuilder();
    ByteString bsTo = ByteString.copyFrom(toAddress);
    ByteString bsOwner = ByteString.copyFrom(ownerAddress);
    builder.setToAddress(bsTo);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    BalanceContract.TransferContract transferContract = builder.build();

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(transferContract);
    if (extention == null || !extention.getResult().getResult()) {
      logger.info("createTransferAssetTransaction2 extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  /**
   * .TRC-10代币转账
   *
   * @param from
   * @param to
   * @param assetName
   * @param amount
   * @return
   * @throws CipherException
   * @throws IOException
   */
  public boolean transferAsset(String from, String to, String assetName, long amount)
      throws CipherException, IOException {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);
    byte[] asset = assetName.getBytes();

    AssetIssueContractOuterClass.TransferAssetContract contract = createTransferAssetContract(
        toAddress, asset, fromAddress, amount);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransferAssetTransaction2(contract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("transferAsset extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  /**
   * 投票超级节点 VoteWitnessContract
   *
   * @param from
   * @param to
   * @param count
   * @return
   */
  public boolean voteWitness(String from, String to, long count) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    WitnessContract.VoteWitnessContract.Builder builder = WitnessContract.VoteWitnessContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));

    WitnessContract.VoteWitnessContract.Vote.Builder voteBuilder = WitnessContract.VoteWitnessContract.Vote
        .newBuilder();
    voteBuilder.setVoteAddress(ByteString.copyFrom(toAddress));
    voteBuilder.setVoteCount(count);
    builder.addVotes(voteBuilder.build());

    GrpcAPI.TransactionExtention extention = rpcCli.voteWitnessAccount2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("voteWitness extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean witnessCreate(String from, String url) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    WitnessContract.WitnessCreateContract.Builder builder = WitnessContract.WitnessCreateContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setUrl(ByteString.copyFrom(url.getBytes()));

    GrpcAPI.TransactionExtention extention = rpcCli.createWitness2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("witnessCreate extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  public boolean participateAssetIssue(String from, String to, String assetName, long amount)
      throws CipherException, IOException {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);
    byte[] asset = assetName.getBytes();

    AssetIssueContractOuterClass.ParticipateAssetIssueContract contract = createParticipateAssetIssueContract(
        toAddress, asset, fromAddress, amount);

    GrpcAPI.TransactionExtention extention = rpcCli
        .createParticipateAssetIssueTransaction2(contract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("participateAssetIssue extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean accountUpdate(String from, String accountName) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    AccountContract.AccountUpdateContract.Builder builder = AccountContract.AccountUpdateContract
        .newBuilder();
    ByteString basAddreess = ByteString.copyFrom(fromAddress);
    ByteString bsAccountName = ByteString.copyFrom(accountName.getBytes());
    builder.setAccountName(bsAccountName);
    builder.setOwnerAddress(basAddreess);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("accountUpdate extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean freezeBalance(String from, int balance, int duration, int resource, String to) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    BalanceContract.FreezeBalanceContract.Builder builder = BalanceContract.FreezeBalanceContract
        .newBuilder();
    ByteString byteAddress = ByteString.copyFrom(fromAddress);
    builder.setOwnerAddress(byteAddress)
        .setFrozenBalance(balance)
        .setFrozenDuration(duration)
        .setResourceValue(resource);
    if (!from.equals(to)) {
      builder.setReceiverAddress(ByteString.copyFrom(toAddress));
    }

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("freezeBalance extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean unfreezeBalance(String from, int resource, String to) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    BalanceContract.UnfreezeBalanceContract.Builder builder = BalanceContract.UnfreezeBalanceContract
        .newBuilder();
    ByteString byteAddress = ByteString.copyFrom(fromAddress);
    builder.setOwnerAddress(byteAddress)
        .setResourceValue(resource)
        .setReceiverAddress(ByteString.copyFrom(toAddress));

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("unfreezeBalance extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean withdrawBalance(String from) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    BalanceContract.WithdrawBalanceContract.Builder builder = BalanceContract.WithdrawBalanceContract
        .newBuilder();
    ByteString byteAddress = ByteString.copyFrom(fromAddress);
    builder.setOwnerAddress(byteAddress);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("withdrawBalance extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  public boolean unfreezeAsset(String from) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    AssetIssueContractOuterClass.UnfreezeAssetContract.Builder builder = AssetIssueContractOuterClass.UnfreezeAssetContract
        .newBuilder();
    ByteString byteAddress = ByteString.copyFrom(fromAddress);
    builder.setOwnerAddress(byteAddress);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("unfreezeAsset extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean updateAsset(String from, String desc, String url, long newLimit,
      long newPublicLimit) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    AssetIssueContractOuterClass.UpdateAssetContract.Builder builder = AssetIssueContractOuterClass.UpdateAssetContract
        .newBuilder();
    ByteString byteAddress = ByteString.copyFrom(fromAddress);
    builder.setOwnerAddress(byteAddress);
    builder.setDescription(ByteString.copyFrom(desc.getBytes()));
    builder.setUrl(ByteString.copyFrom(url.getBytes()));
    builder.setNewLimit(newLimit);
    builder.setNewPublicLimit(newPublicLimit);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("updateAsset extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean proposalCreate(String from, long id, long val) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    HashMap<Long, Long> parametersMap = new HashMap<>();
    parametersMap.put(id, val);

    ProposalContract.ProposalCreateContract.Builder builder = ProposalContract.ProposalCreateContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.putAllParameters(parametersMap);

    GrpcAPI.TransactionExtention extention = rpcCli.proposalCreate(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("proposalCreate extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean proposalApprove(String from, long id, boolean support) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    ProposalContract.ProposalApproveContract.Builder builder = ProposalContract.ProposalApproveContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setProposalId(id);
    builder.setIsAddApproval(support);

    GrpcAPI.TransactionExtention extention = rpcCli.proposalApprove(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("proposalApprove extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  public boolean proposalDelete(String from, long id) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    ProposalContract.ProposalDeleteContract.Builder builder = ProposalContract.ProposalDeleteContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setProposalId(id);

    GrpcAPI.TransactionExtention extention = rpcCli.proposalDelete(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("proposalDelete extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }

  public boolean setAccountId(String from, String id) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    AccountContract.SetAccountIdContract.Builder builder = AccountContract.SetAccountIdContract
        .newBuilder();
    builder.setAccountId(ByteString.copyFrom(id.getBytes()));
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));

    Protocol.Transaction transaction = rpcCli.createTransaction(builder.build());

    try {
      transaction = signTransaction(from, transaction);
    } catch (Exception exception) {
      throw new RuntimeException("setAccountId failed");
    }

    return rpcCli.broadcastTransaction(transaction);
  }


  private AssetIssueContractOuterClass.ParticipateAssetIssueContract createParticipateAssetIssueContract(
      byte[] to, byte[] assertName, byte[] owner, long amount) {
    AssetIssueContractOuterClass.ParticipateAssetIssueContract.Builder builder = AssetIssueContractOuterClass.ParticipateAssetIssueContract
        .newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    return builder.build();
  }


  public static AssetIssueContractOuterClass.TransferAssetContract createTransferAssetContract(
      byte[] to, byte[] assertName, byte[] owner, long amount) {
    AssetIssueContractOuterClass.TransferAssetContract.Builder builder = AssetIssueContractOuterClass.TransferAssetContract
        .newBuilder();
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsName = ByteString.copyFrom(assertName);
    ByteString bsOwner = ByteString.copyFrom(owner);
    builder.setToAddress(bsTo);
    builder.setAssetName(bsName);
    builder.setOwnerAddress(bsOwner);
    builder.setAmount(amount);

    return builder.build();
  }


  /**
   * 发行TRC10 通证.
   *
   * @param from               发行人地址
   * @param name               通信证名称
   * @param abbrName           通行证缩写
   * @param totalSupply        发行总量
   * @param trxNum             通证和 TRX 的最小单位兑换比 （？）
   * @param icoNum             ?
   * @param precision          通证的精度，默认为6, 最小是0，最大是6 (?)
   * @param startTime          通证发行开始时间
   * @param endTime            通证发行结束时间
   * @param freeNetLimit       通证的总免费带宽
   * @param publicFreeNetLimit 每个通证拥有者能使用的免费带宽数
   * @param voteScore          (？)
   * @param description        通行证描述
   * @param url                通证的官方网站地址
   * @param frozenSupply       通证发行者发行的时候指定冻结的通证数
   * @return
   */
  public boolean createAssetIssue(String from, String name, String abbrName, Long totalSupply,
      Integer trxNum,
      Integer icoNum, Integer precision, Long startTime, Long endTime,
      Long freeNetLimit, Long publicFreeNetLimit, Integer voteScore,
      String description, String url, Map<String, String> frozenSupply)
      throws CipherException, IOException {

    AssetIssueContractOuterClass.AssetIssueContract.Builder builder = AssetIssueContractOuterClass.AssetIssueContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(WalletApi.decodeFromBase58Check(from)));
    builder.setName(ByteString.copyFrom(name.getBytes()));
    builder.setAbbr(ByteString.copyFrom(abbrName.getBytes()));

    if (totalSupply <= 0) {
      System.out.println("totalSupply should greater than 0. but really is " + totalSupply);
      return false;
    }
    builder.setTotalSupply(totalSupply);

    if (trxNum <= 0) {
      System.out.println("trxNum should greater than 0. but really is " + trxNum);
      return false;
    }
    builder.setTrxNum(trxNum);

    if (icoNum <= 0) {
      System.out.println("num should greater than 0. but really is " + icoNum);
      return false;
    }
    builder.setNum(icoNum);

    if (precision < 0) {
      System.out.println("precision should greater or equal to 0. but really is " + precision);
      return false;
    }
    builder.setPrecision(precision);

    long now = System.currentTimeMillis();
    if (startTime <= now) {
      System.out.println("startTime should greater than now. but really is startTime("
          + startTime + ") now(" + now + ")");
      return false;
    }
    if (endTime <= startTime) {
      System.out.println("endTime should greater or equal to startTime. but really is endTime("
          + endTime + ") startTime(" + startTime + ")");
      return false;
    }

    if (freeNetLimit < 0) {
      System.out.println("freeAssetNetLimit should greater or equal to 0. but really is "
          + freeNetLimit);
      return false;
    }
    if (publicFreeNetLimit < 0) {
      System.out.println("publicFreeAssetNetLimit should greater or equal to 0. but really is "
          + publicFreeNetLimit);
      return false;
    }

    builder.setStartTime(startTime);
    builder.setEndTime(endTime);
    builder.setVoteScore(voteScore);
    builder.setDescription(ByteString.copyFrom(description.getBytes()));
    builder.setUrl(ByteString.copyFrom(url.getBytes()));
    builder.setFreeAssetNetLimit(freeNetLimit);
    builder.setPublicFreeAssetNetLimit(publicFreeNetLimit);

    for (String daysStr : frozenSupply.keySet()) {
      String amountStr = frozenSupply.get(daysStr);
      long amount = Long.parseLong(amountStr);
      long days = Long.parseLong(daysStr);
      AssetIssueContractOuterClass.AssetIssueContract.FrozenSupply.Builder frozenSupplyBuilder
          = AssetIssueContractOuterClass.AssetIssueContract.FrozenSupply.newBuilder();
      frozenSupplyBuilder.setFrozenAmount(amount);
      frozenSupplyBuilder.setFrozenDays(days);
      builder.addFrozenSupply(frozenSupplyBuilder.build());
    }

    GrpcAPI.TransactionExtention extention = rpcCli.createAssetIssue2(builder.build());

    logger.info("createAssetIssue extention:{}", extention.getResult());

    return processTransactionExtention(from, extention);

  }

  public boolean witnessUpdate(String from, String url) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);

    WitnessContract.WitnessUpdateContract.Builder builder = WitnessContract.WitnessUpdateContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setUpdateUrl(ByteString.copyFrom(url.getBytes()));

    GrpcAPI.TransactionExtention extention = rpcCli.updateWitness2(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("witnessUpdate extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);

  }


  private boolean processTransactionExtention(String address,
      GrpcAPI.TransactionExtention extention) {
    Protocol.Transaction transaction = extention.getTransaction();
    if (transaction.getRawData().getContractCount() < 1) {
      return false;
    }

    // TODO 这个类型是什么意思
    if (transaction.getRawData().getContract(0).getType()
        == Protocol.Transaction.Contract.ContractType.ShieldedTransferContract) {
      return false;
    }

    try {
      transaction = signTransaction(address, transaction);
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

  private Protocol.Transaction signTransaction(String address, Protocol.Transaction transaction)
      throws CipherException, IOException {

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


  public static String getTransactionHash(Transaction transaction) {
    String txid = ByteArray.toHexString(Sha256Sm3Hash.hash(transaction.getRawData().toByteArray()));
    return txid;
  }


}
