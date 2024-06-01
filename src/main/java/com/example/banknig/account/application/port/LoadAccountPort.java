package com.example.banknig.account.application.port;

import com.example.banknig.account.domain.Account;

import java.util.Optional;

public interface LoadAccountPort {
    Account loadAccount(Long accountId);
}
