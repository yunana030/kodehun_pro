package org.ssh.team2.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssh.team2.domain.Place;
import org.ssh.team2.domain.PlaceCategory;

public interface PlaceSearchRepository {
    Page<Place> searchAll(String type, String keyword, PlaceCategory category, Pageable pageable);
}
