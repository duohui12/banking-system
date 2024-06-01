package com.example.banknig.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
@DisplayName("Account 클래스")
class AccountTest {

    private Long initialBalance = 1000L;
    private Account account = Account.getAccountInstance(1L,initialBalance);

    @Nested
    @DisplayName("withdraw(출금) 메서드는")
    class Describe_withdraw{

        @Nested
        @DisplayName("잔액보다 큰 출금액이 주어지면")
        class Context_with_amount_greater_than_balance{

            private Long amount = 2000L;

            @Test
            @DisplayName("잔액을 업데이트하지 않고, false를 리턴한다")
            void it_does_not_update_balance_and_returns_false(){
                boolean result = account.withdraw(amount);
                Long currentBalance = account.getBalance();

                assertThat(currentBalance).isEqualTo(initialBalance);
                assertThat(result).isFalse();
            }
        }

        @Nested
        @DisplayName("잔액보다 적은 출금액이 주어지면")
        class Context_with_amount_less_than_balance{

            private Long amount = 500L;

            @Test
            @DisplayName("잔액에서 출금액을 뺴고, true를 리턴한다.")
            void it_subtract_amount_from_balance_and_returns_true(){
                boolean result = account.withdraw(amount);
                Long currentBalance = account.getBalance();

                assertThat(currentBalance).isEqualTo(initialBalance-amount);
                assertThat(result).isTrue();
            }
        }

        @Nested
        @DisplayName("잔액과 같은 출금액이 주어지면")
        class Context_with_amount_equal_to_balance{

            private Long amount = 1000L;

            @Test
            @DisplayName("잔액에서 출금액을 빼고, true를 리턴한다.")
            void it_subtract_amount_from_balance_and_returns_true(){
                boolean result = account.withdraw(amount);
                Long currentBalance = account.getBalance();

                assertThat(currentBalance).isEqualTo(initialBalance-amount);
                assertThat(currentBalance).isEqualTo(0L);
                assertThat(result).isTrue();
            }
        }

    }

    @Nested
    @DisplayName("deposit(입금) 메서드는")
    class Describe_deposit{

        @Nested
        @DisplayName("입금액이 주어지면")
        class Context_with_amount{

            private Long amount = 1000L;

            @Test
            @DisplayName("잔액에 입금액을 더하고, true를 리턴한다")
            void it_adds_amount_to_balance_and_returns_true(){
                boolean result = account.deposit(amount);
                Long currentBalance = account.getBalance();

                assertThat(currentBalance).isEqualTo(initialBalance + amount);
                assertThat(result).isTrue();
            }
        }
    }
}
