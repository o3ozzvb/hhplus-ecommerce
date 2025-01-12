package kr.hhplus.be.infrastructure.user;

import kr.hhplus.be.domain.user.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByIdForUpdate(Long couponId);
}
