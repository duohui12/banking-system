package com.example.banking.account.application.service;

import com.example.banking.account.adapter.persistence.AccountPersistenceAdapter;
import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.domain.Account;
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
@DisplayName("SendMoneyService 클래스")
class SendMoneyServiceConcurrencyTest {

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

    @Nested
    @DisplayName("SendMoney 메서드는")
    class Describe_send_money {

        @Nested
        @DisplayName("A계좌에서 B계좌로, 100번의 송금요청을 동시에 보내면")
        class Context_with_100_concurrent_send_money_requests{

            private SendMoneyCommand validCommand = new SendMoneyCommand(sourceAccountId
                                                                        , targetAccountId
                                                                        , sendMoneyAmount);

            @Test
            @DisplayName("A계좌의 잔액은 (A계좌의 잔액-(100*송금액))원이 되고, B계좌의 잔액은 (B계좌의 잔액+(100*송금액))원이 된다.")
            void it_updates_balance() throws InterruptedException {

                int threadCount = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(50);
                CountDownLatch countDownLatch = new CountDownLatch(threadCount);

                for (int i = 1; i <= threadCount; i++) {
                    executorService.submit(() -> {
                        sendMoneyService.sendMoney(validCommand);
                        countDownLatch.countDown();
                    });
                }

                countDownLatch.await();

                Account sourceAccount = accountPersistenceAdapter.loadAccount(sourceAccountId);
                Account targetAccount = accountPersistenceAdapter.loadAccount(targetAccountId);

                assertThat(sourceAccount.getBalance()).isEqualTo(sourceAccountBalance - (threadCount*sendMoneyAmount));
                assertThat(targetAccount.getBalance()).isEqualTo(targetAccountBalance + (threadCount*sendMoneyAmount));

            }
        }

    }

}
