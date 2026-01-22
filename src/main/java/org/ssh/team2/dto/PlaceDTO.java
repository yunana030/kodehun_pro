package org.ssh.team2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ssh.team2.domain.PlaceCategory;
import org.ssh.team2.dto.upload.UploadResultDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private Long id;                // DB PK
    private String placename;
    private PlaceCategory category;
    private String content;
    private String addr;
    private String sido;
    private String username;        // 작성자
    private LocalDateTime regDate;  // 등록일
    private LocalDateTime uDate;    // 수정일
    private int readCount;
    private List<UploadResultDTO> images;
    private int like_count;
}
