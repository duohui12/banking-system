package com.example.banknig.account.application.service;

import com.example.banknig.account.application.port.LoadAccountPort;
import com.example.banknig.account.application.usecase.SendMoneyCommand;
import com.example.banknig.account.application.usecase.SendMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;

    @Override
    public boolean SendMoney(SendMoneyCommand sendMoneyCommand) {

        /* TODO :
        loadAccountPort.loadAccount(sendMoneyCommand.getSourceAccountId)로 sourceAccount 도메인 조회,
        loadAccountPort.loadAccount(sendMoneyCommand.getTargetAccountId)로 targetAccount 도메인 조회,
        sourceAccount에서 withdraw(), targetAccount에서 deposit()
        */

        return true;
    }

}
