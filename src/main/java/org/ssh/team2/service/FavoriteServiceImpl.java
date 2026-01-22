package org.ssh.team2.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssh.team2.domain.Favorite;
import org.ssh.team2.domain.Member;
import org.ssh.team2.domain.Place;
import org.ssh.team2.repository.FavoriteRepository;
import org.ssh.team2.repository.MemberRepository;
import org.ssh.team2.repository.PlaceListRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaceListRepository placeListRepository;


    public boolean favoritePlace(String username, Long placeId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 기존 즐겨찾기 확인 (placeId 기준)
        Favorite existing = favoriteRepository.findAllByMember(member).stream()
                .filter(fav -> fav.getPlace().getId().equals(placeId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            favoriteRepository.delete(existing);
            return false; // 즐겨찾기 해제
        }

        // 새 즐겨찾기 추가
        Place place = placeListRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Favorite favorite = Favorite.builder()
                .member(member)
                .place(place)
                .build();

        favoriteRepository.save(favorite);
        return true; // 즐겨찾기 추가
    }


    public boolean isFavorite(String username, Long placeId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Place place = placeListRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return favoriteRepository.existsByMemberAndPlace(member, place);
    }

    public List<Place> getFavoritesByUser(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return favoriteRepository.findAllByMemberOrderByRegDateDesc(member)
                .stream()
                .map(Favorite::getPlace)
                .collect(Collectors.toList());
    }

}
