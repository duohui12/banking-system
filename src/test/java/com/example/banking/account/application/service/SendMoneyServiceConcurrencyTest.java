package com.example.banking.account.application.service;

import com.example.banking.account.adapter.persistence.AccountPersistenceAdapter;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
        @DisplayName("A계좌에서 B계좌로, 동일한 금액을 100번 송금하면")
        class Context_with_100_one_way_concurrent_requests{

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

        @Nested
        @DisplayName("A계좌에서 B계좌로, B계좌에서 A계좌로 동일한 금액을 100번 송금하면")
        class Context_with_100_two_way_concurrent_requests{

            //비관적락 걸었을 때 데드락 걸리는 상황 생각해보기

            //초기 상태
            //A계좌 잔액 : 5000원, B계좌 잔액 : 5000원
            //A->B 로 50원*100번 요청 => 총 5000원 송금
            //B->A 로 50원*100번 요청 => 총 5000원 송금

            //예상했던 결과
            //A: 5000 - (50*100) + (50*100) = 5000원 (초기 잔액 그대로)
            //B: 5000 - (50*100) + (50*100) = 5000원 (초기 잔액 그대로)

            //실제 결과
            //1.A->B로 송금하는 트랜잭션1 실행, A에 락걸고 B 얻기 위해 대기
            //3.B->A로 송금하는 트랜잭션2 실행, B에 락걸고 A 얻기 위해 대기
            //트랜잭션1과 트랜잭션2가 각각 자원 선점하고 무한대기

            //h2는 쿼리에 타임아웃 설정x


            private SendMoneyCommand sourceToTargetCommand =new SendMoneyCommand(sourceAccountId
                                                                                    , targetAccountId
                                                                                    , sendMoneyAmount);

            private SendMoneyCommand targetToSourceCommand =new SendMoneyCommand(targetAccountId
                                                                                , sourceAccountId
                                                                                , sendMoneyAmount);

            @Test
            @Timeout(5)
            @DisplayName("A계좌의 잔액과, B계좌의 잔액은 그대로이다.")
            void it_updates_balance_to_the_same_value_as_before() throws InterruptedException {

                int threadCount = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(50);
                CountDownLatch countDownLatch = new CountDownLatch(threadCount);

                for (int i = 1; i <= threadCount; i++) {
                    executorService.submit(() -> {
                        sendMoneyService.sendMoney(sourceToTargetCommand);
                        sendMoneyService.sendMoney(targetToSourceCommand);
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


