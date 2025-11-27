package org.siwoong.muse.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.repository.FollowRepository;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public long countFollowers(Long userId) {
        return followRepository.countByToUserId(userId);
    }

    public long countFollowing(Long userId) {
        return followRepository.countByFromUserId(userId);
    }

    public boolean isFollowing(Long fromUserId, Long toUserId) {
        if (fromUserId == null || toUserId == null) return false;
        if (fromUserId.equals(toUserId)) return false;
        return followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
    }

    @Transactional
    public void follow(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신은 팔로우할 수 없습니다.");
        }

        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        User toUser = userRepository.findById(toUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."));

        if (followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            return; // 이미 팔로우 중이면 조용히 무시
        }

        Follow follow = Follow.builder()
            .fromUser(fromUser)
            .toUser(toUser)
            .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long fromUserId, Long toUserId) {
        followRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
            .ifPresent(followRepository::delete);
    }

    public List<Follow> getFollowers(Long userId) {
        return followRepository.findByToUserId(userId);
    }

    public List<Follow> getFollowing(Long userId) {
        return followRepository.findByFromUserId(userId);
    }
}
