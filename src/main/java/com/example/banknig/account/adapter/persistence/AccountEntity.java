package com.example.banknig.account.adapter.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@AllArgsConstructor
@NotNull
class AccountEntity {

    private Long id;
    private Long balance;
}
