package org.ssh.team2.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
    private Long rno;
    @NotEmpty
    private String content;
    @NotEmpty
    private String username;
//    private String profileImg;
    // 장소 댓글
    private Long placeId;
    // 자유게시판 댓글
    private Long freeId;
    // 댓글 구분(free/place)
    private String type;
    private LocalDateTime regDate;
    private LocalDateTime upDate;
}
