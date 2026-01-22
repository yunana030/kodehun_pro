package org.ssh.team2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.ssh.team2.config.auth.MemberPrincipal;
import org.ssh.team2.domain.Member;
import org.ssh.team2.repository.MemberRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
public class UploadAvatarController {

    private final MemberRepository memberRepository;
    @Value("${org.ssh.team2.upload.path}")
    private String uploadRoot;

    public UploadAvatarController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/account/avatar")
    public String upload(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
                         @RequestParam("avatar") MultipartFile file,
                         HttpServletRequest req) throws Exception {
        log.info("aaaaaaaaaaaaaaaaaaaaaaa"+file.getOriginalFilename());
        log.info(memberPrincipal.getUsername() + "memberPrincipal");
        String referer = Optional.ofNullable(req.getHeader("Referer")).orElse("/main");


        // 1) null 가드 최우선
        if (memberPrincipal == null || memberPrincipal.getUsername() == null) {
            return "redirect:" + referer;
        }

        // 2) 이제부터 안전하게 사용 가능
        String username = memberPrincipal.getUsername();
        //log.info(file.getOriginalFilename()+"업로드 완료!!!!!!!!!!!!!!!!!!!!!"+rincipal.getMember().getUsername());

        // ★ 진입 로그
        log.info("avatar upload called. principal={}, fileSize={}",
                (memberPrincipal != null ? memberPrincipal.getUsername(): "null"),
                (file != null ? file.getSize() : -1));


        if (file == null || file.isEmpty())          return "redirect:" + referer;
        log.info(uploadRoot+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (file.getSize() > 2_000_000)              return "redirect:" + referer; // 2MB 제한

        String ct = Optional.ofNullable(file.getContentType()).orElse("");
        if (!ct.startsWith("image/")) return "redirect:" + referer;

        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf('.') + 1).toLowerCase())
                .filter(e -> List.of("png","jpg","jpeg","gif","webp").contains(e))
                .orElse("png");

        Path dir = Paths.get(uploadRoot+"/avatars").toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String filename = username + "." + ext;
        Path dst = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
        }

        String url = "/avatars/" + filename + "?v=" + System.currentTimeMillis();

        log.info("Avatar saved: {}", dst);
        log.info("Session avatarUrl: {}", url);

        // db저장
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + username));
        member.setAvatarUrl(url);
        memberRepository.save(member);

        // SecurityContext 갱신
        MemberPrincipal updatedPrincipal = new MemberPrincipal(member);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                updatedPrincipal,
                memberPrincipal.getPassword(),
                memberPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 세션 저장
        req.getSession().setAttribute("avatarUrl", url);

        return "redirect:" + referer;
    }
}
