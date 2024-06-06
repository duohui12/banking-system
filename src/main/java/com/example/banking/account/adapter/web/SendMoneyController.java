package com.example.banking.account.adapter.web;

import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.service.SendMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyService sendMoneyService;

    @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    public void sendMoney(Long sourceAccountId
                            , Long targetAccountId
                            , Long amount){

        SendMoneyCommand sendMoneyCommand = new SendMoneyCommand(
                                            sourceAccountId
                                            , targetAccountId
                                            , amount );

        sendMoneyService.sendMoney(sendMoneyCommand);
    }

}

