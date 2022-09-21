pragma solidity ^0.8.0;

contract PersonalSmartContract {

    uint a = 0;
    uint256 amount = 0;
    constructor() public payable {
    }

    function getA() public view returns (uint) {
        return a;
    }

    function setA(uint newA) external {
        a = newA;
    }

    function getAmount() public view returns (uint) {
        return amount;
    }

    receive() external payable {
        amount += msg.value;
    }

}
