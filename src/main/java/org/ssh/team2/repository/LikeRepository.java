package org.ssh.team2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Like;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByMemberIdAndPlaceId(Long memberId, Long placeId);
}
