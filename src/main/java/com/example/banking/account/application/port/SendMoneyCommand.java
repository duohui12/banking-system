package com.example.banking.account.application.port;

import com.example.banking.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

    @NotNull
    private final Long sourceAccountId;

    @NotNull
    private final Long targetAccountId;

    @NotNull
    @Range(min = 1L)
    private final Long amount;

    public SendMoneyCommand(Long sourceAccountId
                            , Long targetAccountId
                            , Long amount)
    {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.validateSelf();
    }

}
