package org.ssh.team2.service;


import org.ssh.team2.domain.*;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.ReplyDTO;

public interface ReplyService {
    Long insertReply(ReplyDTO replyDTO);
    ReplyDTO findReplyById(Long rno);
    void modifyReply(ReplyDTO replyDTO);
    void deleteReply(Long rno);
    PageResponseDTO<ReplyDTO> getListOfBoard(String type, Long placeId, Long freeId, PageRequestDTO pageRequestDTO);

    default Reply dtoToEntity(ReplyDTO replyDTO, Member member, Place place, Free free) {
        Reply reply = Reply.builder()
                .rno(replyDTO.getRno())
                .content(replyDTO.getContent())
                .member(member)
                .place(place)
                .free(free)
                .type(ReplyType.valueOf(replyDTO.getType()))
                .build();
        return reply;
    }
    default ReplyDTO entityToDto(Reply reply) {
        ReplyDTO.ReplyDTOBuilder builder = ReplyDTO.builder()
                .rno(reply.getRno())
                .content(reply.getContent())
                .username(reply.getMember().getUsername())
                .regDate(reply.getRegDate())
                .upDate(reply.getUpDate());

        if (reply.getType() == ReplyType.PLACE && reply.getPlace() != null) {
            builder.placeId(reply.getPlace().getId());
        } else if (reply.getType() == ReplyType.FREE && reply.getFree() != null) {
            builder.freeId(reply.getFree().getId());
        }

        return builder.build();
    }
}
