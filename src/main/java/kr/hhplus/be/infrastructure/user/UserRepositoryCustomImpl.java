package kr.hhplus.be.infrastructure.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.domain.user.entity.QUser;
import kr.hhplus.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByIdForUpdate(Long id) {
        QUser user = QUser.user;

        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(user.id.eq(id))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // 비관적 락 설정
                        .fetchOne()
        );
    }
}
