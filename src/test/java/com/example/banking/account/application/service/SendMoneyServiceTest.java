package com.example.banking.account.application.service;

import com.example.banking.account.application.port.LoadAccountPort;
import com.example.banking.account.application.port.SaveAccountPort;
import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.domain.Account;
import com.example.banking.exception.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

    }
}
