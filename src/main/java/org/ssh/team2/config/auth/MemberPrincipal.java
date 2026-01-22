package org.ssh.team2.config.auth;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.ssh.team2.domain.Member;

import java.util.Collection;
import java.util.List;

@Data
@Getter
@RequiredArgsConstructor
public class MemberPrincipal implements UserDetails {
    private Member member;

    public MemberPrincipal(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = member.getRole();
        if (role != null && !role.startsWith("ROLE_")) role = "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // ===== Thymeleaf에서 사용할 커스텀 프로퍼티 =====
    public String getDisplayName() { return member.getName(); }
    public String getEmail() { return member.getEmail(); }
    public String getRoleName() {
        String role = member.getRole();
        return (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;
    }

    
//    계정 상태를 체크하는 플래그
//    계정이 만료되지 않았는가
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
//    계정이 잠기지 않았는가
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
//    비밀번호가 오래되지 않았는가
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
//    계정이 활성화 되어 있는가
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 로그 찍을 때 객체 내용을 쉽게 확인 가능
    @Override
    public String toString() {
        return "MemberPrincipal{username=" + getUsername() + "}";
    }

    // 아바타 이미지
    public String getAvatarUrl() {
        return member.getAvatarUrl();
    }
}
