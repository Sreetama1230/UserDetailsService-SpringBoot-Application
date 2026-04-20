package com.userdetailsservice.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.userdetailsservice.app.model.ServiceRequests;
import com.userdetailsservice.app.model.UserDetails;

public interface ServiceRequestsRepo extends JpaRepository<ServiceRequests, Long> {

	@Query("Select s from ServiceRequests s where requestId = :requestId")
	Optional<ServiceRequests> findByRequestId( @Param("requestId") String requestId);
	
	@Query("Select s from ServiceRequests s where entityId = :entityId")
	Optional<ServiceRequests> findByEntityId( @Param("entityId") UserDetails entityId);

}
