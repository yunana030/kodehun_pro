package org.ssh.team2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    // 일반 회원 조회
    List<Member> findByRole(String role);
    Optional<Member> findByUsername(String username);        // Optional 제거
    boolean existsByUsername(String username);
    // 페이징 적용해서 일반 회원 조회
    Page<Member> findByRole(String role, Pageable pageable);
}
