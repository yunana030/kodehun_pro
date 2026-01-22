package org.ssh.team2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.ssh.team2.domain.Reply;
import org.ssh.team2.domain.ReplyType;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    // type 별로 댓글 조회 (place, free)
    @Query("""
            select r from Reply r
            where r.type = :type
            and (
            (:type = 'PLACE' and r.place.id = :placeId)
            or (:type = 'FREE' and r.free.id = :freeId)
            )
            """)
    Page<Reply> findByTypeAndBoardId(String type, Long placeId, Long freeId, Pageable pageable);

    // type별 댓글 삭제
    @Modifying
    @Query("""
            delete from Reply r
            where r.type = :type
            and (
            (:type = 'PLACE' and r.place.id = :placeId)
            or (:type = 'FREE' and r.free.id = :freeId)
            )
            """)
    void deleteByTypeAndBoardId(String type, Long placeId, Long freeId);
    Page<Reply> findByFreeId(Long freeId, Pageable pageable);
    Page<Reply> findByPlaceId(Long placeId, Pageable pageable);
    Page<Reply> findByTypeAndFreeId(ReplyType type, Long freeId, Pageable pageable);
    Page<Reply> findByTypeAndPlaceId(ReplyType type, Long placeId, Pageable pageable);
}
