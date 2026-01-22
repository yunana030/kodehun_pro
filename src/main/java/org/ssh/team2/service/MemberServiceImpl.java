package org.ssh.team2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.MemberRegisterDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.PlaceListRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PlaceListRepository placeListRepository;
    @Override
    public void registerMember(Member member) {
        memberRepository.save(member);
    }

    @Override
    public List<Member> getAllUsers() {
        return memberRepository.findByRole("USER"); // 모든 회원 조회
    }

    @Override
    public Member getMember(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Override
    public void updateMember(Member memberDto) {
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setMphone(memberDto.getMphone());
        member.setRole(memberDto.getRole());

        memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long id) {
        placeListRepository.deleteById(id);
        memberRepository.deleteById(id);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !memberRepository.existsByUsername(username);
    }

    @Override
    public PageResponseDTO<MemberRegisterDTO> getUserPage(PageRequestDTO pageRequestDTO) {
        // Pageable 생성 (페이지, 사이즈, 정렬)
        Pageable pageable = pageRequestDTO.getPageable("id"); // id 기준 내림차순

        // 회원(User)만 조회 (관리자는 제외)
        Page<Member> result = memberRepository.findByRole("USER", pageable);
        // member -> MemberRegisterDTO 변환
        List<MemberRegisterDTO> dtoList = result.getContent().stream()
                .map(member ->{
                    MemberRegisterDTO dto = new MemberRegisterDTO();
                    dto.setId(member.getId());
                    dto.setUsername(member.getUsername());
                    dto.setName(member.getName());
                    dto.setEmail(member.getEmail());
                    dto.setMphone(member.getMphone());
                    dto.setRole(member.getRole());
                    return dto;
                }).collect(Collectors.toList());

        //pageResponseDTO 생성
        return PageResponseDTO.<MemberRegisterDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

}
