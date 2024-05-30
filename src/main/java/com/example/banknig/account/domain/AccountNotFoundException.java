package com.example.banknig.account.domain;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long accountId){
        super("Account not found exception : "  + accountId);
    }
}
