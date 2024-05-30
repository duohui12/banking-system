package com.example.banknig.account.adapter.persistence;

import com.example.banknig.account.application.port.LoadAccountPort;
import com.example.banknig.account.domain.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryAccountRepository implements LoadAccountPort {

    @Override
    public Optional<Account> loadAccount(Long accountId) {
        return null;
    }

}
