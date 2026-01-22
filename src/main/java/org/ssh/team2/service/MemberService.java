package org.ssh.team2.service;

import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.MemberRegisterDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;

import java.util.List;

public interface MemberService {
    void registerMember(Member member);
    List<Member> getAllUsers();
    Member getMember(Long id);
    void updateMember(Member member);
    void deleteMember(Long id);
    boolean isUsernameAvailable(String username);

    // 페이징 적용 회원 조회
    PageResponseDTO<MemberRegisterDTO> getUserPage(PageRequestDTO pageRequestDTO);
}