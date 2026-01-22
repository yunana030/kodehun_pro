package org.ssh.team2.service;

import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.domain.tbl_image;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.PlaceDTO;
import org.ssh.team2.dto.PlaceListPageRequestDTO;
import org.ssh.team2.dto.upload.UploadResultDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface PlaceService {
//    id와 일치하는 장소 게시글 상세보기
    PlaceDTO getPlaceById(Long id, Integer mode);
//    장소 게시글 + 이미지 파일까지 등록
    Long registerPlace(PlaceDTO dto, Member member);  // 추가
//    카테고리 클릭 시 해당 카테고리만 보이게
    List<PlaceDTO> getPlacesByCategory(PlaceCategory category);
//  장소 게시글 수정
    void modifyPlace(PlaceDTO dto);
//   장소 게시글 삭제
    void deletePlace(Long id);
    // 게시글 ID로 DTO 가져오기 (이미지 등 기존 정보 확인용)
    PlaceDTO findPlaceById(Long id);
//  장소 게시글 리스트
    List<PlaceDTO> getAllPlaces();
//  장소 게시글 리스트(페이징 추가)
    PageResponseDTO<PlaceDTO> getPlacePageList(PlaceListPageRequestDTO pageRequestDTO);
    PageResponseDTO<?> getRepliesByPlaceId(Long placeId, PageRequestDTO pageRequestDTO);
    // 관리자 페이지용 페이징 추가 - 이상호
    PageResponseDTO<PlaceDTO> getPlaceList(PageRequestDTO pageRequestDTO);
    // 내가쓴글조회
    PageResponseDTO<PlaceDTO> getMyPlaces(Member member, PageRequestDTO pageRequestDTO);

    // 좋아요
    int getLikeCount(Long id);

//    DTO <-> Entity 변환
    // DTO -> Entity 변환 (글 등록, 삭제 시 사용)
    default Place dtoToEntity(PlaceDTO dto) {
        Place place = Place.builder()
                .id(dto.getId())
                .placename(dto.getPlacename())
                .category(dto.getCategory())
                .content(dto.getContent())
                .addr(dto.getAddr())
                .sido(dto.getSido())
                .build();

        if(dto.getImages() != null){
            dto.getImages().forEach(file ->
                    place.addImage(file.getUuid(), file.getFileName(), file.isImage())
            );
        }
        return place;
    }

    // Entity -> DTO 변환 (조회/리스트용)
    default PlaceDTO entityToDto(Place place) {
        PlaceDTO dto = PlaceDTO.builder()
                .id(place.getId())
                .placename(place.getPlacename())
                .category(place.getCategory())
                .content(place.getContent())
                .addr(place.getAddr())
                .sido(place.getSido())
                .username(place.getMember() != null ? place.getMember().getUsername() : "알 수 없음")
                .regDate(place.getRegDate())
                .uDate(place.getUpDate())
                .readCount(place.getReadcount())
                .build();

        if(place.getImages() != null){
            List<UploadResultDTO> imageDTOs = place.getImages().stream()
                    .sorted((a,b) -> Integer.compare(a.getOrd(), b.getOrd())) // ord 순서대로 정렬
                    .map(img -> UploadResultDTO.builder()
                            .uuid(img.getUuid())
                            .fileName(img.getFilename())
                            .image(img.isImage())
                            .build())
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }
        return dto;
    }

    default UploadResultDTO imgEntityToDTO(tbl_image img) {
        return UploadResultDTO.builder()
                .uuid(img.getUuid())
                .fileName(img.getFilename())
                .image(img.isImage())
                .build();
    }

}