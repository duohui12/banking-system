package com.example.banknig.account.application.port;

import com.example.banknig.account.domain.Account;

public interface SaveAccountPort {
    void saveAccount(Account account);
}
