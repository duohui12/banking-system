package com.example.banking.account.application.port;

import com.example.banking.account.domain.Account;

public interface SaveAccountPort {
    void saveAccount(Account account);
}
