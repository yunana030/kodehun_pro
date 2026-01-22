// src/main/java/org/ssh/team2/service/MyPageService.java
package org.ssh.team2.service;

import org.ssh.team2.dto.MyPageDTO;

public interface MyPageService {
    MyPageDTO loadMyInfo(String username);
    void updateMyInfo(String username, MyPageDTO dto);
    void changePassword(String username, String currentPassword, String newPassword);
    void withdrawHard(String username);
}
