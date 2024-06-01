package com.example.banknig.account.application.service;

import com.example.banknig.account.application.port.LoadAccountPort;
import com.example.banknig.account.application.port.SendMoneyCommand;
import com.example.banknig.account.application.port.SaveAccountPort;
import com.example.banknig.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SendMoneyService{

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    public boolean sendMoney(SendMoneyCommand sendMoneyCommand) {

        //TODO: 아래 시나리오 생각해보기 - 개선할 부분
        // A계좌의 잔액 : 4000원, B계좌의 잔액 : 1000원, C계좌의 잔액 : 4000원
        // A계좌에서 B계좌로 1000원을 송금하고, C계좌에서 B계좌로 1000원을 송금해서
        // 최종적으로 A:3000원, B:3000원, C:3000원 => 이런상태가 되게하고 싶다.
        // 아래 순서대로 거래가 이루어졌다고 가정해보자
        // 1.A계좌의 잔액 read(4000), 1000원 출금하고 남은 잔액 3000원 write (A:3000)
        // 2.B계좌의 잔액 read(1000), A계좌에서 보낸 1000원 더해서 잔액 2000원으로 계산함. 아직 write는 안함 (B:1000)
        // 3.C계좌의 잔액 read(4000), 1000원 출금하고 남은 잔액 3000원 write (C:3000)
        // 4.B계좌의 잔액 read(1000), C계좌에서 보낸 1000원 더해서 잔액 2000원으로 계산하고 write (B:2000)
        // 5.2번에서 계산해두었던 B계좌의 잔액 2000원 write (B:2000)
        // 6.최종 잔고 상태 A: 3000원, B:2000원, C:3000원
        // 하나의 송금 트랜잭션이 실행되는 도중에 같은 자원을 사용하는 다른 트랜잭션이 실행되어 lost update

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
