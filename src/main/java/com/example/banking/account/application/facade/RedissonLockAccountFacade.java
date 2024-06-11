package com.example.banking.account.application.facade;

import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.service.SendMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockAccountFacade {

    private final SendMoneyService sendMoneyService;
    private final RedissonClient redissonClient;

    public boolean sendMoney(SendMoneyCommand sendMoneyCommand) throws InterruptedException {

        boolean result;
        Long account1;
        Long account2;

        if(sendMoneyCommand.getSourceAccountId() < sendMoneyCommand.getTargetAccountId()){
            account1 = sendMoneyCommand.getSourceAccountId();
            account2 = sendMoneyCommand.getTargetAccountId();
        }else {
            account1 = sendMoneyCommand.getTargetAccountId();
            account2 = sendMoneyCommand.getSourceAccountId();
        }

        RLock lock1 = redissonClient.getLock(account1.toString());
        RLock lock2 = redissonClient.getLock(account2.toString());

        try{
            boolean availableLock1 = lock1.tryLock(10, 15, TimeUnit.SECONDS);
            boolean availableLock2 = lock2.tryLock(10, 5, TimeUnit.SECONDS);

            if(!availableLock1 || !availableLock2){
                log.info("lock 획득 실패");
                return false;
            }

            result = sendMoneyService.sendMoney(sendMoneyCommand);
        }catch(InterruptedException ex){
            log.info("에러발생");
            throw ex;
        }finally {
            lock1.unlock();
            lock2.unlock();
        }

        return result;

    }
}
