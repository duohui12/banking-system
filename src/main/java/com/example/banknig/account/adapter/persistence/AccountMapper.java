package com.example.banknig.account.adapter.persistence;

import com.example.banknig.account.domain.Account;
import org.springframework.stereotype.Component;

@Component
class AccountMapper {

    //Entity -> Domain 변환
    Account mapToDomain(AccountEntity accountEntity){
        return Account.getAccountInstance(accountEntity.getId(), accountEntity.getBalance());
    }

    //Domain -> Entity 변환
    AccountEntity mapToEntity(Account account){
        return new AccountEntity(account.getAccountId(), account.getBalance());
    }

}



