package com.example.banking.account.adapter.persistence;

import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.domain.Account;
import com.example.banking.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
//@Repository
class InMemoryAccountRepository implements LoadAccountPort
                                                    , SaveAccountPort {

    private final AccountMapper accountMapper;
    private static Map<Long,AccountEntity> map = new ConcurrentHashMap<>();
    private static Long accountId = 0L;


    @Override
    public Account loadAccount(Long accountId) {
        AccountEntity accountEntity  = findById(accountId)
                                             .orElseThrow(()-> new AccountNotFoundException(accountId));

        return accountMapper.mapToDomain(accountEntity);
    }

    @Override
    public void saveAccount(Account account) {
        if(account.getAccountId() == null){
            account.setAccountId(getAccountId());
        }
        map.put(account.getAccountId(),accountMapper.mapToEntity(account));
    }

    private Optional<AccountEntity> findById(Long id){
        return Optional.ofNullable(map.get(id));
    }

    private synchronized Long getAccountId(){
        accountId += 1L;
        return accountId;
    }
}
