package org.ssh.team2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Free;
import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.repository.search.FreeSearch;

public interface FreeRepository extends JpaRepository<Free, Long>, FreeSearch {
    // 내가쓴글조회위해
    Page<Free> findByMember(Member member, Pageable pageable);
}
