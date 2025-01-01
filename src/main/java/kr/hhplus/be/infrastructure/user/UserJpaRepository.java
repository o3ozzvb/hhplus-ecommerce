package kr.hhplus.be.infrastructure.user;

import kr.hhplus.be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository  extends JpaRepository<User, Long> {
}
