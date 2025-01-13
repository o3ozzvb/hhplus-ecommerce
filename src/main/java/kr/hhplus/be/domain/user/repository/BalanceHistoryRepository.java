package kr.hhplus.be.domain.user.repository;

import kr.hhplus.be.domain.user.entity.BalanceHistory;

public interface BalanceHistoryRepository {
    BalanceHistory save(BalanceHistory balanceHistory);
}
