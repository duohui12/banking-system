package com.example.banking.account.adapter.persistence;

import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.domain.Account;
import com.example.banking.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccountPersistenceAdapter  implements LoadAccountPort
                                                    , SaveAccountPort {

    private final SpringDataJpaAccountRepository springDataJpaAccountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account loadAccount(Long accountId) {
        AccountEntity accountEntity = springDataJpaAccountRepository.findById(accountId)
                                            .orElseThrow(() -> new AccountNotFoundException(accountId));

        return accountMapper.mapToDomain(accountEntity);
    }

    @Override
    public void saveAccount(Account account) {
        springDataJpaAccountRepository.save(accountMapper.mapToEntity(account));
    }

}
