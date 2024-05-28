package com.example.banknig.account.adapter.persistence;

import com.example.banknig.account.application.port.LoadAccountPort;
import com.example.banknig.account.domain.Account;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAccountRepository implements LoadAccountPort {

    @Override
    public Account loadAccount(Long accountId) {
        return null;
    }
}
