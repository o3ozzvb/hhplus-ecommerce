package kr.hhplus.be.infrastructure.user.jpa;

import kr.hhplus.be.domain.user.entity.BalanceHistory;
import kr.hhplus.be.infrastructure.user.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryJpaRepository extends JpaRepository<BalanceHistory, Long>, UserRepositoryCustom {
}
