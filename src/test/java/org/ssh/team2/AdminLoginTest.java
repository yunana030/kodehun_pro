package org.ssh.team2;

import groovy.util.logging.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.ssh.team2.repository.AdminRepository;

@SpringBootTest
@Log4j2
public class AdminLoginTest {
    @Autowired
    private AdminRepository adminRepository;

    @Test
    public void insertAdmin(){

        Admin admin = Admin.builder()
                .aid("admin")
                .password("1234")
                .username("관리자")
                .email("123@afdsadf")
                .aphone("123123")
                .build();
        adminRepository.save(admin);
    }
}
