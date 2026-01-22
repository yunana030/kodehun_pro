package org.ssh.team2.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssh.team2.domain.Free;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.Reply;
import org.ssh.team2.dto.FreeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.PlaceDTO;
import org.ssh.team2.repository.FreeRepository;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.ReplyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class FreeServiceImpl implements FreeService {
    @Autowired
    private FreeRepository freeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Override
    public Long insertFree(FreeDTO freeDTO, Member member) {
        Free free = dtoToEntity(freeDTO, member);
        member = memberRepository.findByUsername(freeDTO.getWriter())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));;
        free.setMember(member);
        Long id = freeRepository.save(free).getId();
        return id;
    }

    @Override
    public List<FreeDTO> findAllFree() {
        List<Free> free = freeRepository.findAll();
        List<FreeDTO> dtos = new ArrayList<>();
        for (Free f : free) {
            dtos.add(entityToDto(f));
        }
        return dtos;
    }

    @Override
    public FreeDTO findFreeById(Long id, Integer mode) {
        Free free = freeRepository.findById(id).orElse(null);
        if(mode==1){
            free.updateReadCount();
            freeRepository.save(free);
        }
        FreeDTO dto = entityToDto(free);
        dto.setWriter(free.getMember().getUsername());
        return dto;
    }

    @Override
    public void updateFree(FreeDTO freeDTO) {

        Free free = freeRepository.findById(freeDTO.getId()).orElse(null);
        free.change(freeDTO.getTitle(), freeDTO.getContent());
    }

    @Override
    public void deleteFree(Long id) {
        Free free = freeRepository.findById(id).orElse(null);
        freeRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<FreeDTO> getList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<Free> result = freeRepository.searchAll(
                pageRequestDTO.getTypes(),
                pageRequestDTO.getKeyword(),
                pageable);

        List<FreeDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
        int total = (int)result.getTotalElements();

        PageResponseDTO<FreeDTO> responseDTO = PageResponseDTO.<FreeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();

        return responseDTO;
    }

    @Override
    public PageResponseDTO<?> getRepliesByFreeId(Long freeId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("rno");
        Page<Reply> replyPage = replyRepository.findByFreeId(freeId, pageable);

        return PageResponseDTO.<Reply>withAll()
                .dtoList(replyPage.getContent())
                .pageRequestDTO(pageRequestDTO)
                .total((int)replyPage.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<FreeDTO> getMyFrees(Member member, PageRequestDTO pageRequestDTO) {
        // Repository에서 Page<Place> 가져오기
        Page<Free> result = freeRepository.findByMember(member, pageRequestDTO.getPageable("id"));

        // Entity -> DTO 변환
        List<FreeDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)   // default 메서드 재사용
                .collect(Collectors.toList());

        // total 개수
        int total = (int) result.getTotalElements();

        // PageResponseDTO 생성
        return new PageResponseDTO<>(pageRequestDTO, dtoList, total);
    }
}
