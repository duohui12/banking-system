package com.example.banknig.account.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE) //모든 필드를 포함한 private 생성자.
@Getter
@Setter
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

    public static Account getAccountInstance(Long accountId, Long balance){
        return new Account(accountId, balance);
    }

}


