package org.ssh.team2.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.ssh.team2.config.auth.MemberPrincipal;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.dto.*;
import org.ssh.team2.repository.PlaceListRepository;
import org.ssh.team2.service.FavoriteService;
import org.ssh.team2.service.FreeService;
import org.ssh.team2.service.MyPageService;
import org.ssh.team2.service.PlaceService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")  // 기본 prefix: /mypage
public class MyPageController {
    private final FavoriteService favoriteService;
    private final MyPageService myPageService;
    private final PlaceService placeService;
    private final FreeService freeService;

    /** 마이페이지 메인 (내 정보 보기) - URL: GET /mypage */
    @GetMapping({"", "/"})  // 또는 @GetMapping("/")
    public String myPage(Authentication auth, Model model,
                         @RequestParam(value = "saved", required = false) String saved,
                         @RequestParam(value = "pwdChanged", required = false) String pwdChanged) {   // 인증 체크 (비로그인 접근 시 로그인 페이지로)
        // 인증 체크 (비로그인 접근 시 로그인 페이지로)
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return "redirect:/member/member_login";
        }

        MyPageDTO dto = myPageService.loadMyInfo(auth.getName());
        model.addAttribute("myPageDTO", dto);

        // PRG로 붙여준 플래그 표시용(선택)
        if (saved != null) model.addAttribute("saved", true);
        if (pwdChanged != null) model.addAttribute("pwdChanged", true);

        return "mypage/mypage"; // templates/mypage/mypage.html
    }

    /** 내 정보 수정 저장 - URL: POST /mypage/update */
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("myPageDTO") MyPageDTO myPageDTO,
                         BindingResult bindingResult,
                         Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return "redirect:/member/member_login";
        }
        if (bindingResult.hasErrors()) {
            // 검증 에러 시에는 같은 화면으로(필드 에러 표시)
            return "mypage/mypage";
        }
        myPageService.updateMyInfo(auth.getName(), myPageDTO);
        // PRG 패턴
        return "redirect:/mypage?saved=true";
    }

    /** 비밀번호 변경 - URL: POST /mypage/password */
    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Authentication auth,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return "redirect:/member/member_login";
        }
        try {
            myPageService.changePassword(auth.getName(), currentPassword, newPassword);
            ra.addFlashAttribute("pwdChanged", true);
        } catch (IllegalArgumentException e) {
            // 비번 불일치 등 사용자 메시지
            ra.addFlashAttribute("pwdError", e.getMessage());
        }
        return "redirect:/mypage";
    }

    /** 회원 탈퇴(soft delete) - URL: POST /mypage/withdraw */
    @PostMapping("/withdraw")
    public String withdraw(Authentication auth,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return "redirect:/member/member_login";
        }
        // ★ 하드딜리트 호출
        myPageService.withdrawHard(auth.getName());

        // ★ 여기서 바로 로그아웃 처리
        new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler()
                .logout(request, response, auth);

        return "redirect:/main";  // 홈으로
    }

    //    즐겨찾기 목록 추가
    @GetMapping("/favoritelist")
    public String myFavorites(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return "redirect:/member/member_login";
        }

        String username = auth.getName();

        // 즐겨찾기한 Place 리스트를 DTO로 변환
        List<PlaceDTO> favoritePlacesDTO = favoriteService.getFavoritesByUser(username)
                .stream()
                .map(place -> PlaceDTO.builder()
                        .id(place.getId())
                        .placename(place.getPlacename())
                        .category(place.getCategory())
                        .sido(place.getSido())
                        .build())
                .toList();

        model.addAttribute("favoritePlaces", favoritePlacesDTO);
        return "mypage/favoritelist"; // templates/mypage/favoritelist.html
    }

    /** 내가 쓴 글 - URL: GET /mypage/written */
    @GetMapping("/written")
    public String written(
            @AuthenticationPrincipal MemberPrincipal userDetails,
            @RequestParam(defaultValue = "1") int placePage,
            @RequestParam(defaultValue = "1") int freePage,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // 로그인한 회원 엔티티
        Member member = userDetails.getMember();

        PageRequestDTO placeReq = PageRequestDTO.builder().page(placePage).size(size).build();
        PageRequestDTO freeReq = PageRequestDTO.builder().page(freePage).size(size).build();

        PageResponseDTO<PlaceDTO> placeResponse = placeService.getMyPlaces(member, placeReq);
        PageResponseDTO<FreeDTO> freeResponse = freeService.getMyFrees(member, freeReq);

        model.addAttribute("placeResponse", placeResponse);
        model.addAttribute("freeResponse", freeResponse);
        model.addAttribute("placePage", placePage);
        model.addAttribute("freePage", freePage);
        model.addAttribute("size", size);

        return "mypage/written";
    }
}