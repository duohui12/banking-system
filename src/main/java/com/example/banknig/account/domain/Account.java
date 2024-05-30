package com.example.banknig.account.domain;


public class Account {

    private Long accountId;
    private Long balance;


    //출금
    public boolean withdraw(Long amount){
        if(!mayWithdraw(amount)){
            return false;
        }
        balance -= amount;
        return true;
    }

    //출금가능여부 확인
    private boolean mayWithdraw(Long amount){
        return balance >= amount;
    }

    //입금
    public boolean deposit(Long amount){
        balance += amount;
        return true;
    }

}


