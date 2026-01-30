package org.ssh.team2;

import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.ssh.team2.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@SpringBootTest
//@Log4j2
//public class AdminLoginTest {
//    @Autowired
//    private AdminRepository adminRepository;
//
//    @Test
//    public void insertAdmin(){
//
//        Admin admin = Admin.builder()
//                .aid("admin")
//                .password("1234")
//                .username("관리자")
//                .email("123@afdsadf")
//                .aphone("123123")
//                .build();
//        adminRepository.save(admin);
//    }
//
//}

@SpringBootTest
public class AdminLoginTest {

    @Test
    public void printEncryptedPassword() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        String rawPassword = "1234";
        String encoded = encoder.encode(rawPassword);

        // 콘솔 출력
        System.out.println("암호화된 비밀번호: " + encoded);
    }
}
