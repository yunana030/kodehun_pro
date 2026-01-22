package org.ssh.team2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.*;
import org.ssh.team2.repository.FreeRepository;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.NoticeRepository;
import org.ssh.team2.service.FreeService;
import org.ssh.team2.service.MemberService;
import org.ssh.team2.service.PlaceService;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MemberService memberService;
    private final FreeService freeService;
    private final PlaceService placeService;
    private final MemberRepository memberRepository;
    private final FreeRepository freeRepository;
    private final NoticeRepository noticeRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long memberCount = memberRepository.count();
        long freeCount = freeRepository.count();
        long noticeCount = noticeRepository.count();

        long totalPosts = freeCount + noticeCount;

        model.addAttribute("memberCount", memberCount);
        model.addAttribute("totalPosts", totalPosts);

        return "admin/dashboard";
    }

    @GetMapping("/admin_login")
    public String loginPage() {
        return "admin/admin_login";
    }

    @GetMapping("/member_list/ajax")
    public String memberListAjax(PageRequestDTO pageRequestDTO, Model model) {
        // 서비스 호출 -> USER 회원만 페이징 처리
        PageResponseDTO<MemberRegisterDTO> responseDTO = memberService.getUserPage(pageRequestDTO);

        // 모델에 추가
        model.addAttribute("memberPage", responseDTO);          // Thymeleaf에서 memberPage.dtoList 사용 가능
        model.addAttribute("pageRequestDTO", pageRequestDTO); // 페이지 정보 보관

        // fragment 반환 (Ajax에서 가져올 HTML)
        return "admin/member_list :: member_table";
    }

    @DeleteMapping("/member/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "ok";
    }

    @GetMapping("/member/{id}")
    @ResponseBody
    public MemberRegisterDTO getMember(@PathVariable Long id) {
        Member m = memberService.getMember(id);
        MemberRegisterDTO dto = new MemberRegisterDTO();
        dto.setId(m.getId());
        dto.setUsername(m.getUsername());
        dto.setName(m.getName());
        dto.setEmail(m.getEmail());
        dto.setMphone(m.getMphone());
        dto.setRole(m.getRole()); // Enum이라면 문자열로 변환
        return dto;
    }

    @PutMapping("/member/{id}")
    @ResponseBody
    public String updateMember(@PathVariable Long id, @RequestBody Member dto) {
        // 보안/검증: id 일치 검사 (옵션)
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return "id-mismatch";
        }

        memberService.updateMember(dto);
        return "ok";
    }

    // 기본 ajax 호출
    @GetMapping("/free/list/ajax")
    public String getFreeListAjax(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<FreeDTO> responseDTO = freeService.getList(pageRequestDTO);

        model.addAttribute("freePage", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "admin/free_list :: free_table"; // fragment 반환
    }

    @DeleteMapping("/free/{id}")
    @ResponseBody
    public String deleteFree(@PathVariable Long id) {
        freeService.deleteFree(id);
        return "ok";
    }

    @GetMapping("/place/list/ajax")
    public String getPlaceListAjax(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<PlaceDTO> responseDTO = placeService.getPlaceList(pageRequestDTO);

        model.addAttribute("placePage", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "admin/place_list :: place_table"; // fragment 반환
    }

    @DeleteMapping("/place/{id}")
    @ResponseBody
    public String deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return "ok";
    }
}
