package org.ssh.team2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.domain.Reply;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.PlaceDTO;
import org.ssh.team2.dto.PlaceListPageRequestDTO;
import org.ssh.team2.dto.upload.UploadResultDTO;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.PlaceListRepository;
import org.ssh.team2.repository.ReplyRepository;
import org.ssh.team2.repository.search.PlaceSearchRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true) // 기본은 읽기 전용 (성능 최적화)
public class PlaceServiceImpl implements PlaceService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaceListRepository placeListRepository;
    @Autowired
    private PlaceSearchRepository placeSearchRepository;
    @Autowired
    private ReplyRepository replyRepository;

    //    장소글 상세보기용
    @Override
    @Transactional    // 조회수는 수정 개념!
    public PlaceDTO getPlaceById(Long id, Integer mode) {
        Place place = placeListRepository.findByIdWithMember(id)
            .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        if(mode != null && mode == 1) {
            place.upReadcount(); // 조회수 증가 (더티 체킹으로 자동 저장)
        }
    
        return entityToDto(place);
    }

    //장소 게시글 등록
    @Override
    public Long registerPlace(PlaceDTO dto, Member  member) {
//        시큐리티 추가 이후 \ 글등록시 회원아이디 안 적게끔 회원 정보 받아옴
        Place place = dtoToEntity(dto); // dtoToEntity 안에서 addImage까지 처리
        place.setMember(member);

        return placeListRepository.save(place).getId(); // 딱 한 줄!
    }

    //카테고리에 해당하는 게시글 리스트만 보이게
    @Override
    public List<PlaceDTO> getPlacesByCategory(PlaceCategory category) {
        List<Place> places = placeListRepository.findByCategory(category);
        return places.stream()
                .map(this::entityToDto)   // Entity -> DTO 변환
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void modifyPlace(PlaceDTO dto) {
        Place place = placeListRepository.findById(dto.getId()).orElse(null);
//        변경하려는 영역
        place.setPlacename(dto.getPlacename());
        place.setCategory(dto.getCategory());
        place.setContent(dto.getContent());
        place.setAddr(dto.getAddr());
        place.setSido(dto.getSido());

        // 이미지 수정 처리
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            // 기존 이미지 제거
            place.removeImages();
            // 새 이미지 추가
            for (UploadResultDTO imgDTO : dto.getImages()) {
                place.addImage(imgDTO.getUuid(),
                        imgDTO.getFileName(),
                        imgDTO.isImage());
            }
        }
        // placeListRepository.save(place);    // 더티채킹! save가 없어도 메소드 종료시점에 자동으로 db 반영됨
    }

    @Override
    public void deletePlace(Long id) {
        Place place = placeListRepository.findById(id).orElse(null);
        // 엔티티 내부에서 이미지 제거한 후에
        place.removeImages();
        // 디비에서 삭제까지
        placeListRepository.delete(place);
    }

    // @Override
    // public PlaceDTO findPlaceById(Long id) {
    //     Place place = placeListRepository.findByIdWithMember(id)
    //             .orElseThrow(() -> new IllegalArgumentException("Place not found"));
    //     return entityToDto(place);
    // }

//    장소 게시글 리스트
    @Override
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeListRepository.findAll();
        return places.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

//    장소 게시글 리스트 with 페이징
    @Override
    public PageResponseDTO<PlaceDTO> getPlacePageList(PlaceListPageRequestDTO pageRequestDTO) {


        Pageable pageable = pageRequestDTO.getPageable("id"); // 또는 regDate

        Page<Place> result;
        result = placeSearchRepository.searchAll(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getCategory(),
                pageRequestDTO.getPageable("id")
        );

        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        int total = (int) result.getTotalElements();

        return PageResponseDTO.<PlaceDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    public PageResponseDTO<?> getRepliesByPlaceId(Long placeId, PageRequestDTO  pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("rno");
        Page<Reply> replyPage = replyRepository.findByPlaceId(placeId, pageable);

        return PageResponseDTO.<Reply>withAll()
                .dtoList(replyPage.getContent())
                .pageRequestDTO(pageRequestDTO)
                .total((int)replyPage.getTotalElements())
                .build();
    }

    // 좋아요 카운트
    @Override
    public int getLikeCount(Long id) {
        return placeListRepository.findById(id)
                .map(Place::getLike_count)
                .orElse(0);
    }

    // 관리자페이지 페이징 -이상호
    @Override
    public PageResponseDTO<PlaceDTO> getPlaceList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<Place> result = placeListRepository.findAll(pageable);

        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        int total = (int) result.getTotalElements();

        return PageResponseDTO.<PlaceDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

    @Override
    public PageResponseDTO<PlaceDTO> getMyPlaces(Member member, PageRequestDTO pageRequestDTO) {
        // Repository에서 Page<Place> 가져오기
        Page<Place> result = placeListRepository.findByMember(member, pageRequestDTO.getPageable("id"));

        // Entity -> DTO 변환
        List<PlaceDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)   // default 메서드 재사용
                .collect(Collectors.toList());

        // total 개수
        int total = (int) result.getTotalElements();

        // PageResponseDTO 생성
        return new PageResponseDTO<>(pageRequestDTO, dtoList, total);
    }

}
