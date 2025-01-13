package kr.hhplus.be.infrastructure.user.repository;

import kr.hhplus.be.domain.user.entity.BalanceHistory;
import kr.hhplus.be.domain.user.repository.BalanceHistoryRepository;
import kr.hhplus.be.infrastructure.user.jpa.BalanceHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BalanceHistoryRepositoryImpl implements BalanceHistoryRepository {

    private final BalanceHistoryJpaRepository balanceHistoryJpaRepository;

    @Override
    public BalanceHistory save(BalanceHistory balanceHistory) {
        return balanceHistoryJpaRepository.save(balanceHistory);
    }

}
