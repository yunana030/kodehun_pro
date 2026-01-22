package org.ssh.team2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssh.team2.domain.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
