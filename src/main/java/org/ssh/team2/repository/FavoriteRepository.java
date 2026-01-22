package org.ssh.team2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Favorite;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
//    특정 회원 + 장소로 즐겨찾기 여부 확인
    Optional<Favorite> findByMemberAndPlace(Member member, Place place);

    // 특정 회원 + 장소 즐겨찾기 존재 여부만 확인
    boolean existsByMemberAndPlace(Member member, Place place);

    // 즐찾 해제
    void deleteByMemberAndPlace(Member member, Place place);

    // 마이페이지 -> 자기가 즐찾한 리스트 보여줌
    List<Favorite> findAllByMember(Member member);
    List<Favorite> findAllByMemberOrderByRegDateDesc(Member member);


}
