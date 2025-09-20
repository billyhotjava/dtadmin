package com.yuzhi.dtadmin.repository;

import com.yuzhi.dtadmin.domain.ChangeRequest;
import com.yuzhi.dtadmin.domain.enumeration.ChangeResourceType;
import com.yuzhi.dtadmin.domain.enumeration.ChangeStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findByStatus(ChangeStatus status);
    List<ChangeRequest> findByStatusAndResourceType(ChangeStatus status, ChangeResourceType resourceType);
    List<ChangeRequest> findByRequestedByOrderByRequestedAtDesc(String requestedBy);
}
