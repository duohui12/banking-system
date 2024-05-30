package com.example.banknig.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long accountId){
        super("Account not found exception : "  + accountId);
    }
}
