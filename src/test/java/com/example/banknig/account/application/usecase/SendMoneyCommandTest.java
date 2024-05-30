package com.example.banknig.account.application.usecase;

import com.example.banknig.account.application.port.SendMoneyCommand;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SendMoneyCommandTest {

    @Nested
    @DisplayName("SendMoneyCommand는")
    class Describe_SendMoneyCommand {

        Long validSourceAccountId = 12345L;
        Long validTargetAccountId = 54321L;
        Long validAmount = 100L;


        @Nested
        @DisplayName("인스턴스 생성시 null인 SourceAccountId가 주어지면")
        class Context_With_Null_SourceAccountId{

            Long invalidSourceAccountId = null;

            @Test
            @DisplayName("Validation Exception을 던진다")
            void it_throws_validation_exception(){
                assertThrows( ConstraintViolationException.class
                            , () -> new SendMoneyCommand( invalidSourceAccountId
                                                            , validTargetAccountId
                                                                , validAmount) );
            }

        }

        @Nested
        @DisplayName("인스턴스 생성시 null인 TargetAccountId가 주어지면")
        class Context_With_Null_TargetAccountId {

            Long invalidTargetAccountId = null;

            @Test
            @DisplayName("Validation Exception을 던진다")
            void it_throws_validation_exception(){
                assertThrows( ConstraintViolationException.class
                            , () -> new SendMoneyCommand( validSourceAccountId
                                                            , invalidTargetAccountId
                                                            , validAmount) );
            }

        }

        @Nested
        @DisplayName("인스턴스 생성시 null인 금액이 주어지면")
        class Context_with_null_amount{

            Long nullAmount = null;

            @Test
            @DisplayName("Validation Exception을 던진다")
            void it_throws_validation_exception(){
                assertThrows( ConstraintViolationException.class
                        , () -> new SendMoneyCommand( validSourceAccountId
                                , validTargetAccountId
                                , nullAmount) );
            }

        }

        @Nested
        @DisplayName("인스턴스 생성시 0보다 작거나 같은 금액이 주어지면")
        class Context_with_minus_amount{

            Long minusAmount = -100L;
            Long zeroAmount = 0L;


            @Test
            @DisplayName("Validation Exception을 던진다")
            void it_throws_validation_exception(){
                assertThrows( ConstraintViolationException.class
                            , () -> new SendMoneyCommand( validSourceAccountId
                                                        , validTargetAccountId
                                                        , minusAmount ) );

                assertThrows( ConstraintViolationException.class
                            , () -> new SendMoneyCommand( validSourceAccountId
                                                        , validTargetAccountId
                                                        , zeroAmount ) );
            }

        }

        @Nested
        @DisplayName("인스턴스 생성시 Null이 아닌 계좌번호와, 0보다 큰 금액이 주어지면")
        class Context_with_valid_input{

            SendMoneyCommand command = new SendMoneyCommand(validSourceAccountId
                                                            , validTargetAccountId
                                                            , validAmount);

            @Test
            @DisplayName("SendMoneyCommand 인스턴스를 생성한다.")
            void it_throws_validation_exception(){
                assertThat(command).isInstanceOf(SendMoneyCommand.class);
                assertThat(command.getSourceAccountId()).isEqualTo(validSourceAccountId);
                assertThat(command.getTargetAccountId()).isEqualTo(validTargetAccountId);
                assertThat(command.getAmount()).isEqualTo(validAmount);

            }

        }
    }
}
