package kr.hhplus.be.infrastructure.user;

import kr.hhplus.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository  extends JpaRepository<User, Long> {

}
