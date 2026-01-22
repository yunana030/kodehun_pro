package org.ssh.team2.service;

import org.ssh.team2.domain.Place;

import java.util.List;

public interface FavoriteService {

    boolean favoritePlace(String username, Long placeId);
    boolean isFavorite(String username, Long placeId);
    List<Place> getFavoritesByUser(String username);
}
