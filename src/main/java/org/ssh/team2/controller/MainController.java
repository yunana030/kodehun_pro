package org.ssh.team2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.PlaceDTO;
import org.ssh.team2.dto.PlaceListPageRequestDTO;
import org.ssh.team2.service.PlaceService;

@Controller
public class MainController {
    @Autowired
    private PlaceService placeService;
//    //    메인홈이동
    @GetMapping({"/", "/main"})
    public String main(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        PlaceListPageRequestDTO dto = new PlaceListPageRequestDTO();
        dto.setPage(page);
        dto.setSize(3);
        dto.setCategory(PlaceCategory.from(category));   // null → 전체

        PageResponseDTO<PlaceDTO> response = placeService.getPlacePageList(dto);

        model.addAttribute("places", response.getDtoList());
        model.addAttribute("category", dto.getCategory());   // Enum 객체
        model.addAttribute("pageInfo", response);

        model.addAttribute("pageId", "MAIN");   // 필요하면 추가

        return "main";   // src/main/resources/templates/main.html
    }

    @GetMapping("/layout/mypage_layout")
    public void mypage() {}
}

