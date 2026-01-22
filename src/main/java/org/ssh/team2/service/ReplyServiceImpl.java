package org.ssh.team2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssh.team2.domain.*;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.ReplyDTO;
import org.ssh.team2.repository.FreeRepository;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.PlaceListRepository;
import org.ssh.team2.repository.ReplyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final PlaceListRepository placeListRepository;
    private final MemberRepository memberRepository;
    private final FreeRepository freeRepository;

    @Override
    public Long insertReply(ReplyDTO replyDTO) {
        Member member = memberRepository.findByUsername(replyDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));;
        Place place = null;
        Free free = null;

        if("PLACE".equalsIgnoreCase(replyDTO.getType())){
            place = placeListRepository.findById(replyDTO.getPlaceId())
                    .orElse(null);
        } else if("FREE".equalsIgnoreCase(replyDTO.getType())){
            free = freeRepository.findById(replyDTO.getFreeId())
                    .orElse(null);
        }

        Reply reply = dtoToEntity(replyDTO, member, place, free);
        Long rno = replyRepository.save(reply).getRno();
        return rno;
    }

    @Override
    public ReplyDTO findReplyById(Long rno) {
        Reply reply = replyRepository.findById(rno).orElse(null);
        return entityToDto(reply);
    }

    @Override
    public void modifyReply(ReplyDTO replyDTO) {
        Reply reply = replyRepository.findById(replyDTO.getRno()).orElse(null);
        reply.setContent(replyDTO.getContent());
        replyRepository.save(reply);
    }

    @Override
    public void deleteReply(Long rno) {
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(String type, Long placeId, Long freeId, PageRequestDTO pageRequestDTO) {
        ReplyType replyType = ReplyType.valueOf(type);

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("rno").descending()   // 최신순
        );

        Page<Reply> result;

        if (replyType == ReplyType.FREE && freeId != null) {
            result = replyRepository.findByTypeAndFreeId(replyType, freeId, pageable);
        } else if (replyType == ReplyType.PLACE && placeId != null) {
            result = replyRepository.findByTypeAndPlaceId(replyType, placeId, pageable);
        } else {
            result = Page.empty();
        }

        List<ReplyDTO> dtoList=result.getContent().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .total((int)result.getTotalElements())
                .dtoList(dtoList)
                .build();
    }
}
