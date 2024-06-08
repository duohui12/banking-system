package com.example.banking.account.application.facade;

import com.example.banking.account.adapter.persistence.RedisLockRepository;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.service.SendMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LettuceLockAccountFacade {

    private final SendMoneyService sendMoneyService;
    private final RedisLockRepository redisLockRepository;

    public boolean sendMoney(SendMoneyCommand sendMoneyCommand) throws InterruptedException {

        /*
        데드락 방지
        Lock 획득 순서를 정해서 순환대기 끊기
        accountID 오름차순으로 Lock 걸기
        */

        Long account1;
        Long account2;
        if(sendMoneyCommand.getSourceAccountId() < sendMoneyCommand.getTargetAccountId()){
            account1 = sendMoneyCommand.getSourceAccountId();
            account2 = sendMoneyCommand.getTargetAccountId();
        }else {
            account1 = sendMoneyCommand.getTargetAccountId();
            account2 = sendMoneyCommand.getSourceAccountId();
        }

        while(!redisLockRepository.lock(account1) || ! redisLockRepository.lock(account2)){
            Thread.sleep(100);
        }

        boolean result = false;
        try{
            result = sendMoneyService.sendMoney(sendMoneyCommand);
        }finally{
            redisLockRepository.unlock(account1);
            redisLockRepository.unlock(account2);
        }

        return result;
    }
}

