package org.ssh.team2.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ssh.team2.dto.FreeDTO;
import org.ssh.team2.dto.NoticeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.service.FreeService;
import org.ssh.team2.service.MemberService;
import org.ssh.team2.service.NoticeService;

import java.security.Principal;

@Controller
@Log4j2
@RequestMapping("/free")
public class FreeController {
    @Autowired
    FreeService freeService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private NoticeService noticeService;

    @GetMapping("register")
    public void register() {
        log.info("register");
    }
    @PostMapping("register")
    public String register(FreeDTO freeDTO, Principal principal) {
        // 로그인한 사용자 이름 가져오기
        String username = principal.getName();
        // FreeDTO의 작성자(writer)에 로그인한 사용자 이름 세팅
        freeDTO.setWriter(username);

        freeService.insertFree(freeDTO, null);
        return "redirect:/free/list";
    }

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO<FreeDTO> responseDTO = freeService.getList(pageRequestDTO);

        // 디버깅 로그
        log.info("page={}, size={}, total={}, start={}, end={}, prev={}, next={}",
                responseDTO.getPage(), responseDTO.getSize(), responseDTO.getTotal(),
                responseDTO.getStart(), responseDTO.getEnd(),
                responseDTO.isPrev(), responseDTO.isNext());
        model.addAttribute("responseDTO",responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        // 공지사항 추가
        model.addAttribute("noticeList", noticeService.getAllNotices());
    }

    @GetMapping("read")
    public void read(Long id, @RequestParam(required = false, defaultValue = "1") Integer mode, PageRequestDTO pageRequestDTO, Model model){
        log.info("read");
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("free", freeService.findFreeById(id, mode));

        // 댓글 목록 가져오기
        PageResponseDTO<?> replyResponseDTO = freeService.getRepliesByFreeId(id, pageRequestDTO); // 페이지 1, size 5
        model.addAttribute("replyResponseDTO", replyResponseDTO);
    }

    @GetMapping("modify")
    public void modify(Long id, Integer mode, PageRequestDTO pageRequestDTO, Model model){
        log.info("read");
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("free", freeService.findFreeById(id, mode));
    }
    @PostMapping("modify")
    public String modify(FreeDTO freeDTO, RedirectAttributes redirectAttributes){
        log.info("modify");
        freeService.updateFree(freeDTO);
        redirectAttributes.addAttribute("id", freeDTO.getId());
        redirectAttributes.addAttribute("mode",1);
        return "redirect:/free/read";
    }

    @GetMapping("remove")
    public String remove(Long id) {
        log.info("remove");
        FreeDTO freeDTO = freeService.findFreeById(id, 2);
        freeService.deleteFree(id);
        return "redirect:/free/list";
    }
    //공지사항 자세히 보기
    @GetMapping("/notice/read")
    public String readNotice(@RequestParam("id") Long id, Model model) {
        NoticeDTO notice = noticeService.readNotice(id);
        model.addAttribute("notice", notice);
        return "free/notice_read";
    }
}
