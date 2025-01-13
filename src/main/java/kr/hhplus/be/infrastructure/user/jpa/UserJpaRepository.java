package kr.hhplus.be.infrastructure.user.jpa;

import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.infrastructure.user.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByIdForUpdate(Long couponId);
}
