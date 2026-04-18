package com.userdetailsservice.app.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.userdetailsservice.app.entityenum.EntityEnum;
import com.userdetailsservice.app.model.ServiceRequests;
import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.repo.ServiceRequestsRepo;
import com.userdetailsservice.app.repo.UserDetailsRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Will not work - don't have h2 configuration 
@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserDetailsRepositoryTest {

	@Autowired
	UserDetailsRepository detailsRepository;

	@Autowired
	ServiceRequestsRepo serviceRequestsRepo;

	private UserDetails userDetails1;
	private UserDetails userDetails2;

	@BeforeEach
	public void setUp() {
		userDetails1 = new UserDetails(1L, "test-user-1", "TEST-ROLE", "testuser1@gmail.com", "9806543219", "IND", "WB",
				"Kolkata");
		userDetails2 = new UserDetails(2L, "test-user-2", "TEST-ROLE", "testuser2@gmail.com", "9806843219", "IND", "M",
				"Pune");

	}

	@AfterEach
	public void destroy() {
		detailsRepository.deleteAll();
		serviceRequestsRepo.deleteAll();
	}

	@Test
	public void testCreate() {

		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219",
				"IND", "WB", "Kolkata");
		String req = UUID.randomUUID().toString();

		UserDetails savedUserDetails = detailsRepository.save(newUserDetails);
		ServiceRequests newRequests = new ServiceRequests(req, savedUserDetails.getId(),
				EntityEnum.USER.getEntityTypeId());

		ServiceRequests savedServiceRequests =	serviceRequestsRepo.save(newRequests);

		Optional<UserDetails> fetchedUser =
		        detailsRepository.findById(savedUserDetails.getId());

		    Optional<ServiceRequests> fetchedRequest =
		        serviceRequestsRepo.findByRequestId(req);
		    
		
		    assertThat(fetchedUser).isPresent();
		    assertThat(fetchedUser.get().getName()).isEqualTo("test-user");

		    assertThat(fetchedRequest).isPresent();
		    assertThat(fetchedRequest.get().getEntityId())
		        .isEqualTo(savedUserDetails.getId());

	}


}
