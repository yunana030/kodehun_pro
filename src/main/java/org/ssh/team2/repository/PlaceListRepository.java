package org.ssh.team2.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.PlaceCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceListRepository extends JpaRepository<Place, Long> {
    List<Place> findAll();
    List<Place> findByCategory(PlaceCategory category);
    Page<Place> findAllByCategory(PlaceCategory category, Pageable pageable);
    // 내가쓴글보기위해
    Page<Place> findByMember(Member member, Pageable pageable);

    @Query("SELECT p FROM Place p JOIN FETCH p.member WHERE p.id = :id")
    Optional<Place> findByIdWithMember(Long id);
    void deleteByMemberId(Long memberId);

}
