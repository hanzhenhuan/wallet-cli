package org.tron.keystore.personal;

import static org.tron.protos.contract.AssetIssueContractOuterClass.AssetIssueContract;
import static org.tron.protos.contract.AssetIssueContractOuterClass.ParticipateAssetIssueContract;
import static org.tron.protos.contract.AssetIssueContractOuterClass.TransferAssetContract;
import static org.tron.protos.contract.AssetIssueContractOuterClass.UnfreezeAssetContract;
import static org.tron.protos.contract.AssetIssueContractOuterClass.UpdateAssetContract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.TransactionExtention;
import org.tron.protos.Protocol;
import org.tron.protos.Protocol.Permission;
import org.tron.protos.contract.AccountContract;
import org.tron.protos.contract.AccountContract.AccountPermissionUpdateContract;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.ProposalContract;
import org.tron.protos.contract.SmartContractOuterClass.ClearABIContract;
import org.tron.protos.contract.SmartContractOuterClass.CreateSmartContract;
import org.tron.protos.contract.SmartContractOuterClass.TriggerSmartContract;
import org.tron.protos.contract.SmartContractOuterClass.UpdateEnergyLimitContract;
import org.tron.protos.contract.SmartContractOuterClass.UpdateSettingContract;
import org.tron.protos.contract.StorageContract.UpdateBrokerageContract;
import org.tron.protos.contract.WitnessContract;
import org.tron.walletserver.WalletApi;

@Slf4j
public class PersonalWalletApiWrapper extends AbstractPersonalApiWrapper {


  public boolean createAccount(String owner, String account) {

    byte[] ownerAddress = WalletApi.decodeFromBase58Check(owner);
    byte[] toAddress = WalletApi.decodeFromBase58Check(account);

    if (ownerAddress == null || toAddress == null) {
      logger.info("createAccount param illegal ");
      return false;
    }
    AccountContract.AccountCreateContract.Builder builder = AccountContract.AccountCreateContract
        .newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(ownerAddress));
    builder.setAccountAddress(ByteString.copyFrom(toAddress));

    GrpcAPI.TransactionExtention extention = rpcCli.createAccount2(builder.build());
    if (extention == null || !extention.getResult().getResult()) {
      logger.info("createAccount extention:{}", extention);
      return false;
    }
    return processTransactionExtention(owner, extention);
  }

  public boolean sendCoin(String owner, String to, Long amount) {
    byte[] ownerAddress = WalletApi.decodeFromBase58Check(owner);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    if (ownerAddress == null || toAddress == null) {
      logger.info("createAccount param illegal ");
      return false;
    }

    BalanceContract.TransferContract.Builder builder = BalanceContract.TransferContract
        .newBuilder();

    builder.setToAddress(ByteString.copyFrom(toAddress));
    builder.setOwnerAddress(ByteString.copyFrom(ownerAddress));
    builder.setAmount(amount);

    BalanceContract.TransferContract transferContract = builder.build();

    GrpcAPI.TransactionExtention extention = rpcCli.createTransaction2(transferContract);
    if (extention == null || !extention.getResult().getResult()) {
      logger.info("createTransferAssetTransaction2 extention:{}", extention);
      return false;
    }
    return processTransactionExtention(owner, extention);
  }

  public boolean transferAsset(String from, String to, String assetName, long amount) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);
    byte[] asset = assetName.getBytes();

    TransferAssetContract contract = createTransferAssetContract(
        toAddress, asset, fromAddress, amount);

    GrpcAPI.TransactionExtention extention = rpcCli.createTransferAssetTransaction2(contract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("transferAsset extention:{}", extention);
      return false;
    }
    return processTransactionExtention(from, extention);
  }


  public boolean voteWitness(String from, String to, long count) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);

    if (fromAddress == null || toAddress == null) {
      logger.info("createAccount param illegal ");
      return false;
    }

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

    if (fromAddress == null) {
      logger.info("createAccount param illegal ");
      return false;
    }

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


  public boolean participateAssetIssue(String owner, String to, String assetName, long amount) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(owner);
    byte[] toAddress = WalletApi.decodeFromBase58Check(to);
    byte[] asset = assetName.getBytes();

    ParticipateAssetIssueContract contract = createParticipateAssetIssueContract(
        toAddress, asset, fromAddress, amount);

    GrpcAPI.TransactionExtention extention = rpcCli
        .createParticipateAssetIssueTransaction2(contract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("participateAssetIssue extention:{}", extention);
      return false;
    }
    return processTransactionExtention(owner, extention);
  }

  public boolean accountUpdate(String from, String accountName) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    if (fromAddress == null) {
      logger.info("accountUpdate param illegal ");
      return false;
    }

    AccountContract.AccountUpdateContract.Builder builder = AccountContract.AccountUpdateContract
        .newBuilder();
    ByteString basAddress = ByteString.copyFrom(fromAddress);
    ByteString bsAccountName = ByteString.copyFrom(accountName.getBytes());
    builder.setAccountName(bsAccountName);
    builder.setOwnerAddress(basAddress);

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

    if (fromAddress == null || toAddress == null) {
      logger.info("freezeBalance param illegal ");
      return false;
    }

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

    if (fromAddress == null || toAddress == null) {
      logger.info("unfreezeBalance param illegal ");
      return false;
    }

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

    if (fromAddress == null) {
      logger.info("withdrawBalance param illegal ");
      return false;
    }

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

    if (fromAddress == null) {
      logger.info("unfreezeAsset param illegal ");
      return false;
    }

    UnfreezeAssetContract.Builder builder = UnfreezeAssetContract
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

    if (fromAddress == null) {
      logger.info("updateAsset param illegal ");
      return false;
    }

    UpdateAssetContract.Builder builder = UpdateAssetContract
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

    if (fromAddress == null) {
      logger.info("proposalCreate param illegal ");
      return false;
    }

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

    if (fromAddress == null) {
      logger.info("proposalApprove param illegal ");
      return false;
    }

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

    if (fromAddress == null) {
      logger.info("proposalDelete param illegal ");
      return false;
    }
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

    if (fromAddress == null) {
      logger.info("setAccountId param illegal ");
      return false;
    }

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




  public boolean createAssetIssue(String from, String name, String abbrName, Long totalSupply,
      Integer trxNum, Integer icoNum, Integer precision, Long startTime, Long endTime,
      Long freeNetLimit, Long publicFreeNetLimit, Integer voteScore,
      String description, String url, Map<String, String> frozenSupply) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    if (fromAddress == null) {
      logger.info("createAssetIssue param illegal ");
      return false;
    }

    AssetIssueContract.Builder builder = AssetIssueContract.newBuilder()
        .setOwnerAddress(ByteString.copyFrom(fromAddress))
        .setName(ByteString.copyFrom(name.getBytes()))
        .setAbbr(ByteString.copyFrom(abbrName.getBytes()))
        .setTotalSupply(totalSupply)
        .setTrxNum(trxNum)
        .setNum(icoNum)
        .setPrecision(precision)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setVoteScore(voteScore)
        .setDescription(ByteString.copyFrom(description.getBytes()))
        .setUrl(ByteString.copyFrom(url.getBytes()))
        .setFreeAssetNetLimit(freeNetLimit)
        .setPublicFreeAssetNetLimit(publicFreeNetLimit);

    Set<String> keySet = frozenSupply.keySet();
    for (String daysStr : keySet) {
      long amount = Long.parseLong(frozenSupply.get(daysStr));
      long days = Long.parseLong(daysStr);

      AssetIssueContract.FrozenSupply.Builder frozenSupplyBuilder = AssetIssueContract.FrozenSupply
          .newBuilder();

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
    if (fromAddress == null) {
      logger.info("witnessUpdate param illegal ");
      return false;
    }
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

  public boolean deployContract(String from, String contractName, String abiStr, String codeStr,
      long feeLimit, long value, long consumeUserResourcePercent, long originEnergyLimit,
      long tokenValue, String tokenId, String libraryAddressPair, String compilerVersion) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    if (fromAddress == null) {
      logger.info("deployContract param illegal ");
      return false;
    }

    CreateSmartContract contract = WalletApi.createContractDeployContract(contractName, fromAddress,
        abiStr, codeStr, value, consumeUserResourcePercent, originEnergyLimit, tokenValue, tokenId,
        libraryAddressPair, compilerVersion);

    TransactionExtention extention = rpcCli.deployContract(contract);
    if (extention == null || !extention.getResult().getResult()) {
      logger.info("deployContract extention:{}", extention);
      return false;
    }

    extention = supplyTransactionLimit(extention, feeLimit);

    return processTransactionExtention(from, extention);
  }



  public boolean triggerContract(String from, String contract, long callValue, byte[] input,
      long feeLimit, long tokenValue, String tokenId, boolean isConstant) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] contractAddress = WalletApi.decodeFromBase58Check(contract);
    if (fromAddress == null || contractAddress == null) {
      logger.info("triggerContract param illegal ");
      return false;
    }

    TriggerSmartContract triggerContract = WalletApi.triggerCallContract(fromAddress,
        contractAddress, callValue, input, tokenValue, tokenId);

    TransactionExtention extention;
    if (isConstant) {
      extention = rpcCli.triggerConstantContract(triggerContract);
    } else {
      extention = rpcCli.triggerContract(triggerContract);
    }

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("triggerContract extention:{}", extention);
      return false;
    }

    extention = supplyTransactionLimit(extention, feeLimit);

    return processTransactionExtention(from, extention);
  }

  public boolean updateSettingContract(String from, String contract,
      long consumeUserResourcePercent) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] contractAddress = WalletApi.decodeFromBase58Check(contract);
    if (fromAddress == null || contractAddress == null) {
      logger.info("updateSettingContract param illegal ");
      return false;
    }
    UpdateSettingContract updateSettingContract = WalletApi.createUpdateSettingContract(
        fromAddress, contractAddress, consumeUserResourcePercent);

    TransactionExtention extention = rpcCli.updateSetting(updateSettingContract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("updateSettingContract extention:{}", extention);
      return false;
    }

    return processTransactionExtention(from, extention);
  }

  public boolean accountPermissionUpdate(String from, String permissionJson) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    if (fromAddress == null) {
      logger.info("accountPermissionUpdate param illegal ");
      return false;
    }

    AccountPermissionUpdateContract accountPermissionContract = createAccountPermissionContract(
        fromAddress, permissionJson);

    TransactionExtention extention = rpcCli.accountPermissionUpdate(accountPermissionContract);

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("accountPermissionUpdate extention:{}", extention);
      return false;
    }

    return processTransactionExtention(from, extention);
  }

  public boolean clearAbiContract(String from, String contract) {

    byte[] fromAddress = WalletApi.decodeFromBase58Check(from);
    byte[] contractAddress = WalletApi.decodeFromBase58Check(contract);
    if (fromAddress == null || contractAddress == null) {
      logger.info("clearAbiContract param illegal ");
      return false;
    }

    ClearABIContract.Builder builder = ClearABIContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setContractAddress(ByteString.copyFrom(contractAddress));

    TransactionExtention extention = rpcCli.clearContractABI(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("clearAbiContract extention:{}", extention);
      return false;
    }

    return processTransactionExtention(from, extention);
  }

  public boolean updateBrokerageContract(String owner, int brokerage) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(owner);
    if (fromAddress == null) {
      logger.info("updateBrokerageContract param illegal ");
      return false;
    }

    UpdateBrokerageContract.Builder builder = UpdateBrokerageContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setBrokerage(brokerage);

    TransactionExtention extention = rpcCli.updateBrokerage(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("updateBrokerageContract extention:{}", extention);
      return false;
    }

    return processTransactionExtention(owner, extention);
  }

  public boolean updateEnergyLimitContract(String owner, String contract, long originEnergyLimit) {
    byte[] fromAddress = WalletApi.decodeFromBase58Check(owner);
    byte[] contractAddress = WalletApi.decodeFromBase58Check(contract);
    if (fromAddress == null || contractAddress == null) {
      logger.info("updateEnergyLimitContract param illegal ");
      return false;
    }

    UpdateEnergyLimitContract.Builder builder = UpdateEnergyLimitContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(fromAddress));
    builder.setContractAddress(ByteString.copyFrom(contractAddress));
    builder.setOriginEnergyLimit(originEnergyLimit);

    TransactionExtention extention = rpcCli.updateEnergyLimit(builder.build());

    if (extention == null || !extention.getResult().getResult()) {
      logger.info("updateEnergyLimitContract extention:{}", extention);
      return false;
    }

    return processTransactionExtention(owner, extention);
  }



  public static AccountPermissionUpdateContract createAccountPermissionContract(byte[] owner,
      String permissionJson) {
    AccountPermissionUpdateContract.Builder builder = AccountPermissionUpdateContract.newBuilder();

    JSONObject permissions = JSONObject.parseObject(permissionJson);
    JSONObject owner_permission = permissions.getJSONObject("owner_permission");
    JSONObject witness_permission = permissions.getJSONObject("witness_permission");
    JSONArray active_permissions = permissions.getJSONArray("active_permissions");

    if (owner_permission != null) {
      Permission ownerPermission = json2Permission(owner_permission);
      builder.setOwner(ownerPermission);
    }
    if (witness_permission != null) {
      Permission witnessPermission = json2Permission(witness_permission);
      builder.setWitness(witnessPermission);
    }
    if (active_permissions != null) {
      List<Permission> activePermissionList = new ArrayList<>();
      for (int j = 0; j < active_permissions.size(); j++) {
        JSONObject permission = active_permissions.getJSONObject(j);
        activePermissionList.add(json2Permission(permission));
      }
      builder.addAllActives(activePermissionList);
    }
    builder.setOwnerAddress(ByteString.copyFrom(owner));
    return builder.build();
  }





}
