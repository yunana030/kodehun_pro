package org.ssh.team2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.MemberRegisterDTO;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.service.MemberService;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/member_login")
    public String loginPage() {
        return "/member/member_login"; // templates/member/member_login.html
    }

    @GetMapping("join")
    public String join(Model model){
        model.addAttribute("member", new MemberRegisterDTO());
        return "member/join";
    }
//    @PostMapping("register")
//    public String register(@Valid Member member, BindingResult bindingResult){
//        // 유효성 검사 실패 시 다시 회원가입 폼으로 이동
//        if (bindingResult.hasErrors()) {
//            return "member/join";
//        }
//        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
//        member.setRole("USER");
//        memberService.registerMember(member);
//        return "redirect:/main";
//    }
    @PostMapping("register")
    public String register(@Valid MemberRegisterDTO memberDto, BindingResult bindingResult){
        // DTO 단에서 유효성/비밀번호 확인 체크 가능
        if (bindingResult.hasErrors() || !memberDto.getPassword().equals(memberDto.getPasswordConfirm())) {
            return "member/join";
        }

        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(bCryptPasswordEncoder.encode(memberDto.getPassword()))
                .name(memberDto.getName())
                .email(memberDto.getEmail())
                .mphone(memberDto.getMphone())
                .role("USER")
                .build();

        memberService.registerMember(member);
        return "redirect:/main";
    }
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username){
        return memberService.isUsernameAvailable(username);
    }

}
