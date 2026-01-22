// src/main/java/org/ssh/team2/service/MyPageServiceImpl.java
package org.ssh.team2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.MyPageDTO;
import org.ssh.team2.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MyPageDTO loadMyInfo(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        MyPageDTO dto = new MyPageDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getMphone()); // DB 컬럼 mphone 매핑 가정
        return dto;
    }

    @Override
    public void updateMyInfo(String username, MyPageDTO dto) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setMphone(dto.getPhone());
        // 엔티티 변경감지(dirty checking)로 자동 반영
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public void withdrawHard(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + username));
        // 하드 삭제
        memberRepository.delete(member);
        // FK 제약/카스케이드 문제를 조기에 드러내려면 flush까지
        memberRepository.flush();
    }
}

