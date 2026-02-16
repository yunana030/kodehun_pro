package org.ssh.team2.controller;
//이 주석이 확인된다면 가장 최근 버전입니다!


import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ssh.team2.config.auth.MemberPrincipal;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.PlaceDTO;
import org.ssh.team2.dto.PlaceListPageRequestDTO;
import org.ssh.team2.dto.upload.UploadFileDTO;
import org.ssh.team2.dto.upload.UploadResultDTO;
import org.ssh.team2.service.FavoriteService;
import org.ssh.team2.service.LikeService;
import org.ssh.team2.service.PlaceService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@Log4j2
@Controller
@RequestMapping("/place")   //디폴트로 입력해야 함
public class PlaceController {
    @Autowired
    private PlaceService placeService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FavoriteService favoriteService;

    @Value("${org.ssh.team2.upload.path}")
    private String uploadPath;


    // 
    // @GetMapping("/list")
    // public String getPlaces(@RequestParam(required = false) String category,  //
    //                         PlaceListPageRequestDTO pageRequestDTO,
    //                         @RequestParam(required = false) String keyword,
    //                         @RequestParam(required = false) String type,
    //                         @RequestParam(required = false) String searchFilter,
    //                         Model model) {

    //     PlaceCategory placeCategory = PlaceCategory.from(category);
    //     pageRequestDTO.setCategory(placeCategory); // DTO는 이넘이라 적용됨

    //     pageRequestDTO.setKeyword(keyword);
    //     pageRequestDTO.setType(type);
    //     pageRequestDTO.setSearchFilter(searchFilter);

    //     PageResponseDTO<PlaceDTO> responseDTO = placeService.getPlacePageList(pageRequestDTO);

    //     model.addAttribute("pageInfo", responseDTO);
    //     model.addAttribute("places", responseDTO.getDtoList());
    //     model.addAttribute("category", placeCategory);
    //     model.addAttribute("keyword", keyword);
    //     model.addAttribute("searchFilter", searchFilter);
    //     model.addAttribute("type", type); // 검색타입 유지,

    //     return "place/place_list";
    // }

    @GetMapping("/list")
    public String getPlaces(PlaceListPageRequestDTO pageRequestDTO, Model model) {
        // 1. 스프링이 URL 파라미터를 pageRequestDTO에 이미 다 채워줌!
        // 2. 서비스 호출 (서비스가 모든 비즈니스 로직 처리)
        PageResponseDTO<PlaceDTO> responseDTO = placeService.getPlacePageList(pageRequestDTO);
    
        // 3. 딱 필요한 것만 모델에 담기
        model.addAttribute("responseDTO", responseDTO); 
    
        return "place/place_list";
    }


    //장소글 상세보기
    @GetMapping("/view/{id}")
    public String getPlaceDetail(@PathVariable Long id,
                                 PlaceListPageRequestDTO requestDTO,
                                 Model model,
                                 @AuthenticationPrincipal MemberPrincipal userDetails) {
        PlaceDTO place = placeService.getPlaceById(id,1);
        model.addAttribute("place", place);

        // 댓글 목록 가져오기
        PageResponseDTO<?> replyResponseDTO = placeService.getRepliesByPlaceId(id, requestDTO);
        model.addAttribute("replyResponseDTO", replyResponseDTO);
        model.addAttribute("requestDTO", requestDTO);

        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "place/place_view"; //
    }


    // 장소글 등록
    @GetMapping("/register")
    public String PlaceRegister(Model model) {
        model.addAttribute("place", new PlaceDTO());
        return "place/place_register"; //
    }
//    // 글 저장(POST) - 이미지랑 같이 등록
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")  //로그인된 사용자만 글작성 가능
    public String registerPlace(PlaceDTO placeDTO, UploadFileDTO uploadFileDTO,
                                @AuthenticationPrincipal MemberPrincipal userDetails) {
//        이 부분 수업 때(리스트 추가)랑 다른데 해보면서 수정하기로..!
        if (uploadFileDTO.getFiles() != null && !uploadFileDTO.getFiles().isEmpty()) {
            placeDTO.setImages(fileUpload(uploadFileDTO));
        }
//        로그인된 Member 가져와야 함, 시큐리티 이후 추가 설정! (글등록할 때 작성자 입력 없애기 위하여)
        Member member = userDetails.getMember();

        // Place + 이미지 저장!
        Long id = placeService.registerPlace(placeDTO, member);
        return "redirect:/place/view/" + id;
    }

//    이미지파일 추가!
    private List<UploadResultDTO> fileUpload(UploadFileDTO uploadFileDTO) {
        List<UploadResultDTO> list = new ArrayList<>();

        if (uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(multiFile -> {
                String originalFileName = multiFile.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalFileName);
                boolean image = false;
                try {
                    multiFile.transferTo(savePath);
                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbnail = new File(uploadPath, "s_" + uuid + "_" + originalFileName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbnail, 200, 200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalFileName)
                        .image(image)
                        .build());
            });
        }
        return list;
    }

//  게시글(with image) 수정
    @PostMapping("modify")
    public String modifyPlace(PlaceDTO placeDTO, UploadFileDTO uploadFileDTO, RedirectAttributes redirectAttributes) {
        List<UploadResultDTO> imageDTOs = null; // 새로운 파일!

        if (uploadFileDTO.getFiles() != null &&
                !uploadFileDTO.getFiles().get(0).getOriginalFilename().equals("")) {

            // 기존 파일 삭제
            PlaceDTO existingPlace = placeService.getPlaceById(placeDTO.getId(), 0);    //mode: 0은 조회수 유지용
            
            List<UploadResultDTO> existingImages = existingPlace.getImages();
            if (existingImages != null && !existingImages.isEmpty()) {
                removeFiles(existingImages); // 기존 파일 삭제
            }
            // 새로 업로드한 파일 정보 얻기
            imageDTOs = fileUpload(uploadFileDTO);
        }
        // DTO에 이미지 정보 세팅
        placeDTO.setImages(imageDTOs);

        // 서비스 호출 → 수정
        placeService.modifyPlace(placeDTO);
        
        redirectAttributes.addAttribute("id", placeDTO.getId());
        return "redirect:/place/view/" + placeDTO.getId();  //상세보기 페이지로 이동

    }
    //t수정 페이지 열기
    @GetMapping("/modify/{id}")
    public String placeModifyPage(@PathVariable Long id, Model model) {
        PlaceDTO placeDTO = placeService.getPlaceById(id,0); // 기존 데이터 조회
        if (placeDTO == null) {
            return "redirect:/place/list"; // 글이 없으면 목록으로
        }
        model.addAttribute("place", placeDTO);
        return "place/place_modify"; // 수정 페이지 Thymeleaf
    }


//    장소 게시글 삭제 with image
    @GetMapping("/delete/{id}")
    public String deletePlace(@PathVariable Long id) {
        // 먼저 엔티티 조회하고
        PlaceDTO placeDTO = placeService.getPlaceById(id, 0);
        if (placeDTO != null) {  // null 체크 추가 -> 삭제할 때 에러 방지
            // 실제 서버 파일 삭제
            List<UploadResultDTO> images = placeDTO.getImages();
            if (images != null && !images.isEmpty()) {
                removeFiles(images);
            }
            // 서비스 호출 → DB 삭제
            placeService.deletePlace(id);
        }
        // 목록으로 이동
        return "redirect:/place/list";
    }

//    게시글(with image) 삭제
    private void removeFiles(List<UploadResultDTO> files) {
        for (UploadResultDTO file : files) {
            String filename = file.getUuid() + "_" + file.getFileName();
            Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
            boolean removed = false;

            try {
                // 원본 파일 삭제
                String contentType = Files.probeContentType(resource.getFile().toPath());
                removed = resource.getFile().delete();

                // 추가된 썸네일 삭제
                if (contentType != null && contentType.startsWith("image")) {
                    String thumbFileName = "s_" + filename;
                    File thumbFile = new File(uploadPath + File.separator + thumbFileName);
                    thumbFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


// ======================================================================================

     // 좋아요
    @PostMapping("/{placeId}/like")
    @ResponseBody
    public int likePlace(@PathVariable Long placeId, Principal principal) {
        if (principal == null) {
            // 로그인 안 된 사용자 → 좋아요 불가
            return -1;  // 프론트에서 -1이면 로그인 필요 처리
        }
//        String username = userDetails.getUsername(); // 로그인한 회원
        return likeService.likePlace(principal.getName(), placeId);
    }
    //좋아요 상태조회
    @GetMapping("/{placeId}/like/status")
    @ResponseBody
    public Map<String, Object> getLikeStatus(@PathVariable Long placeId, Principal principal) {
        int count = placeService.getLikeCount(placeId);
        boolean liked = false;
        if (principal != null) {
            String username = principal.getName();
            liked = likeService.hasUserLiked(username, placeId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("count", count);
        return result;
    }

    //    즐겨찾기!!!!!!
    @PostMapping("/{placeId}/favorite")
    @ResponseBody
    public boolean favoritePlace(@PathVariable Long placeId, Principal principal) {
        if (principal == null) return false;
        return favoriteService.favoritePlace(principal.getName(), placeId);
    }

    // 페이지를 열 때 즐찾이 되어있으면 색깔이 입혀져 나옴!
    @GetMapping("/{placeId}/favorite/status")
    @ResponseBody
    public Map<String, Object> getFavoriteStatus(@PathVariable Long placeId,Principal principal) {
        boolean favorited = false;
        boolean loginRequired = false;
        if (principal != null) {
            favorited = favoriteService.isFavorite(principal.getName(), placeId);
        } else {
            loginRequired = true; // 로그인 안 되어 있음
        }

        return Map.of(
                "favorited", favorited,
                "loginRequired", loginRequired
        );
    }

}
