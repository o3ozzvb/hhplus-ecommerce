package kr.hhplus.be.domain.user.repository;

import kr.hhplus.be.domain.user.entity.User;

public interface UserRepository {
    User save(User user);
    User findById(Long id);
    User findByIdForUpdate(Long id);
}