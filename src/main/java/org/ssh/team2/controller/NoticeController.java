package org.ssh.team2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.ssh.team2.dto.NoticeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.service.NoticeService;

import java.security.Principal;
import java.util.Optional;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
public class NoticeController {
    private final NoticeService noticeService;

    // Ajax로 공지사항 리스트 fragment 반환
    @GetMapping("/list/ajax")
    public String listAjax(PageRequestDTO pageRequestDTO,Model model) {
        PageResponseDTO<NoticeDTO> responseDTO = noticeService.getNoticePageList(pageRequestDTO);
        model.addAttribute("noticePage",responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        return "admin/notice_list :: notice_table"; // fragment 이름
    }

    // 공지사항 등록
    @PostMapping("/register/ajax")
    @ResponseBody
    public String registerAjax(@RequestBody NoticeDTO dto, Principal principal) {
        dto.setWriter(principal.getName()); // 작성자
        noticeService.saveNotice(dto);
        return "ok";
    }

    // 단일 공지사항 조회 (모달용)
    @GetMapping("/{id}")
    @ResponseBody
    public NoticeDTO getNotice(@PathVariable Long id) {
        Optional<NoticeDTO> noticeDTO = noticeService.getNotice(id);
        return noticeDTO.orElse(null); // 없으면 null 반환
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    @ResponseBody
    public String updateNotice(@PathVariable Long id, @RequestBody NoticeDTO dto) {
        if (!id.equals(dto.getId())) return "id-mismatch";
        noticeService.updateNotice(id, dto);
        return "ok";
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "ok";
    }
}
