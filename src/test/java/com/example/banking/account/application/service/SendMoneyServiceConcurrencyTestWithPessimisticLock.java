package com.example.banking.account.application.service;

import com.example.banking.account.adapter.persistence.AccountPersistenceAdapter;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@Nested
@SpringBootTest
@DisplayName("비관적락을 사용한 SendMoneyService 동시성 테스트")
class SendMoneyServiceConcurrencyTestWithPessimisticLock {

    @Autowired
    private SendMoneyService sendMoneyService;

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;


    //fixture
    private static Long sourceAccountId = 12345L;
    private static Long sourceAccountBalance = 5000L;
    private static Long targetAccountId = 54321L;
    private static Long targetAccountBalance = 5000L;
    private static Long sendMoneyAmount = 50L;

    @BeforeEach
    void setup(){
        //잔액 초기화
        accountPersistenceAdapter.saveAccount(Account.getAccountInstance(sourceAccountId,sourceAccountBalance));
        accountPersistenceAdapter.saveAccount(Account.getAccountInstance(targetAccountId,targetAccountBalance));
    }

    @Nested
    @DisplayName("SendMoney 메서드는")
    class Describe_send_money{

        @Nested
        @DisplayName("A계좌에서 B계좌로, 동일한 금액을 100번 송금하면")
        class Context_with_100_one_way_concurrent_requests{

            private SendMoneyCommand validCommand = new SendMoneyCommand(sourceAccountId
                                                                        , targetAccountId
                                                                        , sendMoneyAmount);

            @Test
            @DisplayName("A계좌의 잔액은 (A계좌의 잔액-(100*송금액))원이 되고, B계좌의 잔액은 (B계좌의 잔액+(100*송금액))원이 된다.")
            void it_updates_balance() throws InterruptedException {

                int runCount = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(50);
                CountDownLatch countDownLatch = new CountDownLatch(runCount);

                for (int i = 1; i <= runCount; i++) {
                    executorService.submit(() -> {
                        sendMoneyService.sendMoneyWithPessimisticLock(validCommand);
                        countDownLatch.countDown();
                    });
                }

                countDownLatch.await();

                Account sourceAccount = accountPersistenceAdapter.loadAccount(sourceAccountId);
                Account targetAccount = accountPersistenceAdapter.loadAccount(targetAccountId);

                assertThat(sourceAccount.getBalance()).isEqualTo(sourceAccountBalance - (runCount*sendMoneyAmount));
                assertThat(targetAccount.getBalance()).isEqualTo(targetAccountBalance + (runCount*sendMoneyAmount));

            }
        }

        @Nested
        @DisplayName("A계좌에서 B계좌로, B계좌에서 A계좌로 동일한 금액을 100번 송금하면")
        class Context_with_100_two_way_concurrent_requests{

            private SendMoneyCommand sourceToTargetCommand =new SendMoneyCommand(sourceAccountId
                                                                                    , targetAccountId
                                                                                    , sendMoneyAmount);

            private SendMoneyCommand targetToSourceCommand =new SendMoneyCommand(targetAccountId
                                                                                , sourceAccountId
                                                                                , sendMoneyAmount);

            @Test
            @DisplayName("A계좌의 잔액과, B계좌의 잔액은 그대로이다.")
            void  it_updates_balance_to_the_same_value_as_before() throws InterruptedException {

                int runCount = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(50);
                CountDownLatch countDownLatch = new CountDownLatch(runCount);

                for (int i = 1; i <= runCount; i++) {
                    executorService.submit(() -> {
                        sendMoneyService.sendMoneyWithPessimisticLock(sourceToTargetCommand);
                        sendMoneyService.sendMoneyWithPessimisticLock(targetToSourceCommand);
                        countDownLatch.countDown();
                    });
                }

                countDownLatch.await();

                Account sourceAccount = accountPersistenceAdapter.loadAccount(sourceAccountId);
                Account targetAccount = accountPersistenceAdapter.loadAccount(targetAccountId);

                assertThat(sourceAccount.getBalance()).isEqualTo(sourceAccountBalance);
                assertThat(targetAccount.getBalance()).isEqualTo(targetAccountBalance);

            }
        }

    }

}


