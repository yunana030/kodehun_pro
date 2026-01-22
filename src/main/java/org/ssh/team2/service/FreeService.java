package org.ssh.team2.service;

import org.ssh.team2.domain.Free;
import org.ssh.team2.domain.Member;
import org.ssh.team2.dto.FreeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;

import java.util.List;

public interface FreeService {
    // 게시글 등록
    Long insertFree(FreeDTO freeDTO, Member member);
    // 게시글 전체 조회
    List<FreeDTO> findAllFree();
    // 게시글 단건 조회
    FreeDTO findFreeById(Long id, Integer mode);
    // 게시글 수정
    void updateFree(FreeDTO freeDTO);
    // 게시글 삭제
    void deleteFree(Long id);
    // 게시글 목록을 페이지 단위로 조회
    PageResponseDTO<FreeDTO> getList(PageRequestDTO pageRequestDTO);
    // 댓글가져오기
    PageResponseDTO<?> getRepliesByFreeId(Long freeId, PageRequestDTO pageRequestDTO);
    // 내가쓴글조회위해
    PageResponseDTO<FreeDTO> getMyFrees(Member member, PageRequestDTO pageRequestDTO);

    default Free dtoToEntity(FreeDTO freeDTO, Member member) {
        Free free = Free.builder()
                .id(freeDTO.getId())
                .title(freeDTO.getTitle())
                .content(freeDTO.getContent())
                .member(member)
                .build();
        return free;
    }
    default FreeDTO entityToDto(Free free) {
        FreeDTO freeDTO = FreeDTO.builder()
                .id(free.getId())
                .title(free.getTitle())
                .content(free.getContent())
                .writer(free.getMember().getUsername())
                .readCount(free.getReadCount())
                .regDate(free.getRegDate())
                .upDate(free.getUpDate())
                .build();
        return freeDTO;
    }
}
