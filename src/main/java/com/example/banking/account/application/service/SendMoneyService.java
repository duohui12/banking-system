package com.example.banking.account.application.service;

import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SendMoneyService{

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    public boolean sendMoney(SendMoneyCommand sendMoneyCommand) {

        Account sourceAccount = loadAccountPort.loadAccount(sendMoneyCommand.getSourceAccountId());

        Account targetAccount = loadAccountPort.loadAccount(sendMoneyCommand.getTargetAccountId());

        //source account 에서 출금, target account에 입금
        if(!sourceAccount.withdraw(sendMoneyCommand.getAmount())){
            return false;
        }
        if(!targetAccount.deposit(sendMoneyCommand.getAmount())){
            return false;
        }

        saveAccountPort.saveAccount(sourceAccount);
        saveAccountPort.saveAccount(targetAccount);

        return true;
    }

}
