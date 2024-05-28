package com.example.banknig.account.adapter.web;

import com.example.banknig.account.application.usecase.SendMoneyCommand;
import com.example.banknig.account.application.usecase.SendMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping("/accounts/send/{sourceAccountId/{targetAccountId}/{amount}}")
    public void sendMoney(Long sourceAccountId
                            , Long targetAccountId
                            , int amount){

        SendMoneyCommand sendMoneyCommand = new SendMoneyCommand(
                                            sourceAccountId
                                            , targetAccountId
                                            , amount );

        sendMoneyUseCase.SendMoney(sendMoneyCommand);
    }

}

