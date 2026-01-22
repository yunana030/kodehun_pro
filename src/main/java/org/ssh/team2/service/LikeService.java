package org.ssh.team2.service;

public interface LikeService {
    int likePlace(String username, Long placeId);
    boolean hasUserLiked(String username, Long placeId);
}
