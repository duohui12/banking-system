package com.example.banking.account.adapter.web;

import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.service.SendMoneyService;
import com.example.banking.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SendMoneyController.class)
@DisplayName("SendMoneyController 테스트")
class SendMoneyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SendMoneyService sendMoneyService;

    @Nested
    @DisplayName("Post /accounts/send/{sourceAccountId}/{targetAccountId}/{amount} 는")
    class Describe_send_money_request{

        @Nested
        @DisplayName("존재하지 않는 계좌번호가 주어지면")
        class Context_with_nonexistence_account_id{

            private Long nonexistenceSourceAccountId = 11111L;

            @BeforeEach
            void setup(){
                given(sendMoneyService.sendMoney(any(SendMoneyCommand.class))).
                        willThrow(AccountNotFoundException.class);
            }

            @Test
            @DisplayName("204 No Content를 리턴한다.")
            void it_returns_204() throws Exception {
                String urlTemplate = String.format("/accounts/send/%s/54321/5000",nonexistenceSourceAccountId);
                mockMvc.perform(post(urlTemplate))
                        .andExpect(status().isNoContent());
            }

        }

        @Nested
        @DisplayName("0보다 작거나 같은 amount가 주어지면")
        class Context_with_zero_or_minus_account{

           private Long zeroAmount = 0L;

            @Test
            @DisplayName("400 Bad Request를 리턴한다.")
            void it_returns_400() throws Exception {
                String urlTemplate = String.format("/accounts/send/12345/54321/"+zeroAmount);
                mockMvc.perform(post(urlTemplate))
                        .andExpect(status().isBadRequest());
            }

        }

        //TODO: source account의 잔액보다 큰 송금액이 주어지면

    }

}
