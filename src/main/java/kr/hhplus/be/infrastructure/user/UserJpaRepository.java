package kr.hhplus.be.infrastructure.user;

import kr.hhplus.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByIdForUpdate(Long couponId);
}
