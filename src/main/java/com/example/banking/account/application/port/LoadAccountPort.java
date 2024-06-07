package com.example.banking.account.application.port;

import com.example.banking.account.domain.Account;

public interface LoadAccountPort {
    Account loadAccount(Long accountId);

    Account loadAccountWithPessimisticLock(Long accountId);
}
