package org.ssh.team2.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ssh.team2.domain.Free;

public interface FreeSearch {
    Page<Free> searchAll(String[] type, String keyword, Pageable pageable);
}
