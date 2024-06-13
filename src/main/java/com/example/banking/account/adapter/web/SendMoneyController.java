package com.example.banking.account.adapter.web;

import com.example.banking.account.application.port.SendMoneyCommand;
import com.example.banking.account.application.service.SendMoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyService sendMoneyService;

    @PostMapping ("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    public void sendMoney(@PathVariable("sourceAccountId") Long sourceAccountId,
                          @PathVariable("targetAccountId") Long targetAccountId,
                          @PathVariable("amount") Long amount) {

        SendMoneyCommand sendMoneyCommand = new SendMoneyCommand(
                                            sourceAccountId
                                            , targetAccountId
                                            , amount );

        sendMoneyService.sendMoney(sendMoneyCommand);
    }

}

