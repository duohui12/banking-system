package com.example.banking.account.application.service;

import com.example.banking.account.adapter.persistence.AccountMapper;
import com.example.banking.account.adapter.persistence.InMemoryAccountRepository;
import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.domain.Account;
import com.example.banking.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@Nested
@Slf4j
@DisplayName("SendMoneyService 클래스")
class SendMoneyServiceTest {

    private static LoadAccountPort loadAccountPort;
    private static SaveAccountPort saveAccountPort;
    private static SendMoneyService sendMoneyService;


    //fixture
    private static Long existingSourceAccountId = 12345L;
    private static Long nonexistenceSourceAccountId = 11111L;
    private static Long sourceAccountBalance = 5000L;
    private static Account sourceAccount = Account.getAccountInstance(existingSourceAccountId, sourceAccountBalance);
    private static Long existingTargetAccountId = 54321L;
    private static Long nonexistenceTargetAccountId = 55555L;
    private static Long targetAccountBalance = 5000L;
    private static Account targetAccount = Account.getAccountInstance(existingTargetAccountId, targetAccountBalance);
    private static Long sendMoneyAmount = 50L;


    @BeforeAll
    static void setup(){
        loadAccountPort = mock(LoadAccountPort.class);
        saveAccountPort = mock(SaveAccountPort.class);
        sendMoneyService = new SendMoneyService(loadAccountPort, saveAccountPort);

        given(loadAccountPort.loadAccount(nonexistenceSourceAccountId))
                .willThrow(new AccountNotFoundException(nonexistenceSourceAccountId));

        given(loadAccountPort.loadAccount(existingSourceAccountId))
                .willReturn(sourceAccount);

        given(loadAccountPort.loadAccount(nonexistenceTargetAccountId))
                .willThrow(new AccountNotFoundException(nonexistenceTargetAccountId));

        given(loadAccountPort.loadAccount(existingTargetAccountId))
                .willReturn(targetAccount);
    }

    @Nested
    @DisplayName("SendMoney 메서드는")
    class Describe_send_money{

        @Nested
        @DisplayName("존재하지 않는 source account id가 주어지면")
        class Context_with_nonexistence_source_account{

            private SendMoneyCommand invalidCommand = new SendMoneyCommand(nonexistenceSourceAccountId
                    , existingTargetAccountId
                    , sendMoneyAmount);

            @Test
            @DisplayName("AccountNotFoundException을 던진다")
            void it_throws_account_not_found_exception(){
                assertThrows(AccountNotFoundException.class,
                        () -> sendMoneyService.sendMoney(invalidCommand));

            }
        }

        @Nested
        @DisplayName("존재하지 않는 target account id가 주어지면")
        class Context_with_nonexistence_target_account{

            private SendMoneyCommand invalidCommand = new SendMoneyCommand(existingSourceAccountId
                    , nonexistenceTargetAccountId
                    , sendMoneyAmount);

            @Test
            @DisplayName("AccountNotFoundException을 던진다")
            void it_throws_account_not_found_exception(){
                assertThrows(AccountNotFoundException.class,
                        () -> sendMoneyService.sendMoney(invalidCommand));
            }
        }

        @Nested
        @DisplayName("존재하는 source account id, target account id와 source account의 잔액보다 큰 송금액이 주어지면")
        class Context_with_existing_account_and_invalid_send_amount{

            private SendMoneyCommand validCommand = new SendMoneyCommand(existingSourceAccountId
                    , existingTargetAccountId
                    , sourceAccount.getBalance() + 1000L);

            @Test
            @DisplayName("false 리턴한다")
            void it_returns_true(){
                assertThat(sendMoneyService.sendMoney(validCommand)).isFalse();
            }
        }

        @Nested
        @DisplayName("존재하는 source account id, target account id와 source account의 잔액보다 적은 송금액 주어지면")
        class Context_with_existing_account_and_valid_send_amount{

            private SendMoneyCommand validCommand = new SendMoneyCommand(existingSourceAccountId
                    , existingTargetAccountId
                    , sourceAccount.getBalance() - 1000L );

            @Test
            @DisplayName("true를 리턴한다")
            void it_returns_true(){
                assertThat(sendMoneyService.sendMoney(validCommand)).isTrue();
                verify(saveAccountPort).saveAccount(sourceAccount);
                verify(saveAccountPort).saveAccount(targetAccount);
            }
        }

        @Nested
        @DisplayName("A계좌에서, B계좌로, 100번의 송금요청을 동시에 보내면")
        class Context_with_100_concurrent_send_money_requests{

            //TODO: 모킹해서 테스트하는 방법은 모르겠음
            private InMemoryAccountRepository repository;
            private SendMoneyService sendMoneyService;
            private SendMoneyCommand validCommand;

            @BeforeEach
            void setup(){
                repository = new InMemoryAccountRepository(new AccountMapper());
                repository.saveAccount(sourceAccount);
                repository.saveAccount(targetAccount);

                sendMoneyService = new SendMoneyService(repository, repository);

                validCommand =  new SendMoneyCommand(existingSourceAccountId
                        , existingTargetAccountId
                        , sendMoneyAmount);
            }

            @Test
            @DisplayName("A계좌의 잔액은 (A계좌의 잔액-(100*송금액))원이 되고, B계좌의 잔액은 (B계좌의 잔액+(100*송금액))원이 된다")
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

                Account sourceAccount = repository.loadAccount(existingSourceAccountId);
                Account targetAccount = repository.loadAccount(existingTargetAccountId);

                assertThat(sourceAccount.getBalance()).isEqualTo(sourceAccountBalance - (threadCount*sendMoneyAmount));
                assertThat(targetAccount.getBalance()).isEqualTo(targetAccountBalance + (threadCount*sendMoneyAmount));

//                log.info("source account balance : {} / target account balance : {}"
//                        , sourceAccount.getBalance()
//                        , targetAccount.getBalance());

            }
        }
    }
}
