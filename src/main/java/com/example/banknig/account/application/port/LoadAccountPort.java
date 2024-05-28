package com.example.banknig.account.application.port;

import com.example.banknig.account.domain.Account;

public interface LoadAccountPort {
    Account loadAccount(Long accountId);
}
