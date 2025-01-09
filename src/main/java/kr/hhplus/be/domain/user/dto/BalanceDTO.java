package kr.hhplus.be.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BalanceDTO {
    long userId;
    int balance;
}
