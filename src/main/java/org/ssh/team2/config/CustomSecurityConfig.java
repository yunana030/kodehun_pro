package org.ssh.team2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {
    // 관리자용 시큐리티 설정
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(csrf -> csrf.disable()) // 비활성화, 테스트용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/admin_login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                )
                .formLogin(login -> login
                        .loginPage("/admin/admin_login")   // 관리자 로그인 페이지 지정
                        .loginProcessingUrl("/admin/loginProcess") // 관리자용 처리 URL
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/admin_login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")                 // 관리자 전용 로그아웃
                        .logoutSuccessUrl("/admin/admin_login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                // 403 발생 시 로그인 페이지로 리다이렉트
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/admin/admin_login?accessDenied")
                );
        return http.build();
    }

    // 일반 사용자용 시큐리티 설정
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/free/notice/**","/place/view/**","/free/read/**","/main","/member/**","/place/list/**","/free/list/**","/free/register/**","/notice/read/**","/place/register/**").permitAll() //permitAll은 전체 허용
                                .requestMatchers("/mypage/**").authenticated()//이걸 하려면 로그인 해야함! 여기 전체 정리 필요! 페이지 다 만들어지면 할 예정
//                                .requestMatchers("/member/**").hasRole("USER")
                                .requestMatchers(HttpMethod.GET).permitAll()
                                .requestMatchers(HttpMethod.POST, "/account/avatar").authenticated()
                                .requestMatchers("/place/*/like", "/place/*/like/status").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/member/member_login")
                        .loginProcessingUrl("/member/loginProcess")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/member/member_login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("http://localhost:8089/main")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();

    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configurer(){
        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }
}