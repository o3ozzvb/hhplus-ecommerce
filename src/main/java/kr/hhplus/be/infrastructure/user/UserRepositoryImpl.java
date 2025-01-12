package kr.hhplus.be.infrastructure.user;

import kr.hhplus.be.domain.user.entity.User;
import kr.hhplus.be.domain.user.repository.UserRepository;
import kr.hhplus.be.support.exception.BusinessException;
import kr.hhplus.be.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST));
    }

    @Override
    public User findByIdForUpdate(Long id) {
        return userJpaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST));
    }


}
