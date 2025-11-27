package org.siwoong.muse.user.repository;

import java.util.List;
import java.util.Optional;
import org.siwoong.muse.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    List<User> findTop20ByNicknameContainingIgnoreCase(String keyword);

    boolean existsByNicknameAndIdNot(String nickname, Long id);

}
