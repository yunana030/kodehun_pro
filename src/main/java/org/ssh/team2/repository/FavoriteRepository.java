package org.ssh.team2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Favorite;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    List<Favorite> findAllByMember(Member member);
    boolean existsByMemberAndPlace(Member member, Place place);

    // 마이페이지 -> 자기가 즐찾한 리스트 보여줌
    List<Favorite> findAllByMemberOrderByRegDateDesc(Member member);


}
