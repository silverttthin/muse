package org.siwoong.muse.user.repository;

import java.util.List;
import java.util.Optional;
import org.siwoong.muse.user.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    Optional<Follow> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    long countByToUserId(Long toUserId);   // 팔로워 수
    long countByFromUserId(Long fromUserId); // 내가 팔로우하는 수

    List<Follow> findByFromUserId(Long fromUserId); // 내가 팔로우하는 유저들
    List<Follow> findByToUserId(Long toUserId);     // 나를 팔로우하는 유저들
}
