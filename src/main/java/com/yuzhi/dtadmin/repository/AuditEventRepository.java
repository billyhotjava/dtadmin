package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.AuditEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByTimestampBetween(Instant from, Instant to);
}
