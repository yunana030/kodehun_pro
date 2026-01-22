package org.ssh.team2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssh.team2.domain.Like;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.repository.LikeRepository;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.PlaceListRepository;

import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PlaceListRepository placeListRepository;

    @Override
    public int likePlace(String username, Long placeId) {
        // 회원 조회
        Member member = memberRepository.findByUsername(username).orElseThrow(()->new IllegalStateException("회원 정보가 없습니다."));
        // 게시글 조회
        Place place = placeListRepository.findById(placeId).orElse(null);
        // 이미 좋아요 했는지
        Optional<Like> existingLike = likeRepository.findByMemberIdAndPlaceId(member.getId(), place.getId());

        if (existingLike.isPresent()) {
            // 이미 좋아요 → 취소
            likeRepository.delete(existingLike.get());
            place.setLike_count(place.getLike_count() - 1);
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .member(member)
                    .place(place)
                    .build();
            likeRepository.save(like);
            place.setLike_count(place.getLike_count() + 1);
        }
        placeListRepository.save(place);

        return place.getLike_count(); // 최신 카운트 반환
    }

    @Override
    public boolean hasUserLiked(String username, Long placeId) {
        Member member = memberRepository.findByUsername(username).orElse(null);
        Place place = placeListRepository.findById(placeId).orElse(null);
        return likeRepository.findByMemberIdAndPlaceId(member.getId(), place.getId()).isPresent();
    }
}
