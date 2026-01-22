package org.ssh.team2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.ssh.team2.domain.Notice;
import org.ssh.team2.dto.NoticeDTO;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.repository.NoticeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    // 엔티티 → DTO 변환
    private NoticeDTO toDTO(Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .writer(notice.getWriter())
                .regDate(notice.getRegDate())
                .upDate(notice.getUpDate())
                .readCount(notice.getReadCount())
                .build();
    }
    // DTO → 엔티티 변환
    private Notice toEntity(NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setId(dto.getId());
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setWriter(dto.getWriter());
        notice.setReadCount(dto.getReadCount());
        return notice;
    }
    @Override
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NoticeDTO> getNotice(Long id) {
        return noticeRepository.findById(id).map(this::toDTO);
    }

    @Override
    public NoticeDTO saveNotice(NoticeDTO noticeDTO) {
        Notice notice = toEntity(noticeDTO);
        Notice saved = noticeRepository.save(notice);
        return toDTO(saved);
    }

    @Override
    public void updateNotice(Long id, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. ID=" + id));

        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        noticeRepository.save(notice);
    }

    @Override
    public NoticeDTO readNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        return NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .writer(notice.getWriter())
                .regDate(notice.getRegDate())
                .build();
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public PageResponseDTO<NoticeDTO> getNoticePageList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id"); // DESC 정렬
        Page<Notice> result = noticeRepository.findAll(pageable);

        List<NoticeDTO> dtoList = result.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<NoticeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }
}
