package org.ssh.team2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.tbl_image;

public interface ImageRepository extends JpaRepository<tbl_image, Integer> {
}
