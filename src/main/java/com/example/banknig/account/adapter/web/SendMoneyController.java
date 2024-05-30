package com.example.banknig.account.adapter.web;

import com.example.banknig.account.application.port.SendMoneyCommand;
import com.example.banknig.account.application.service.SendMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyService sendMoneyService;

    @PostMapping("/accounts/send/{sourceAccountId/{targetAccountId}/{amount}}")
    public void sendMoney(Long sourceAccountId
                            , Long targetAccountId
                            , Long amount){

        SendMoneyCommand sendMoneyCommand = new SendMoneyCommand(
                                            sourceAccountId
                                            , targetAccountId
                                            , amount );

        sendMoneyService.SendMoney(sendMoneyCommand);
    }

}

