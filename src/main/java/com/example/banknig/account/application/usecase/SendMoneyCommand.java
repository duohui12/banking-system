package com.example.banknig.account.application.usecase;

import com.example.banknig.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Getter
public class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

    @NotNull
    private final Long sourceAccountId;

    @NotNull
    private final Long targetAccountId;

    @Range(min = 1)
    private final int amount;

    public SendMoneyCommand(Long sourceAccountId
                            , Long targetAccountId
                            , int amount)
    {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.validateSelf();
    }

}
