package org.ssh.team2.service;

import org.ssh.team2.dto.NoticeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;

import java.util.List;
import java.util.Optional;

public interface NoticeService {
    List<NoticeDTO> getAllNotices();        // Notice → NoticeDTO
    Optional<NoticeDTO> getNotice(Long id); // Notice → NoticeDTO
    NoticeDTO saveNotice(NoticeDTO noticeDTO);
    void deleteNotice(Long id);
    void updateNotice(Long id, NoticeDTO noticeDTO);
    NoticeDTO readNotice(Long id);

    PageResponseDTO<NoticeDTO> getNoticePageList(PageRequestDTO pageRequestDTO);
}
