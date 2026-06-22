package com.userdetailsservice.app.units;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import com.userdetailsservice.app.entityenum.EntityEnum;
import com.userdetailsservice.app.exp.UserNotFoundException;
import com.userdetailsservice.app.model.ServiceRequests;
import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.repo.ServiceRequestsRepo;
import com.userdetailsservice.app.repo.UserDetailsRepository;
import com.userdetailsservice.app.service.RedisService;
import com.userdetailsservice.app.service.UserDetailsService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")

public class UserDetailsServiceTest {

	@Mock
	UserDetailsRepository detailsRepository;
	@Mock
	ServiceRequestsRepo serviceRequestsRepo;

	@InjectMocks
	UserDetailsService detailsService;
	
	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private RedisService redisService;

	@Test
	public void testCreate() {

		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		String req = UUID.randomUUID().toString();
		UserDetails savedUserDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");

		ServiceRequests savedRequests = new ServiceRequests(1L, req, savedUserDetails,
				EntityEnum.USER.getEntityTypeId());

		when(detailsRepository.save(newUserDetails)).thenReturn(savedUserDetails);

		when(serviceRequestsRepo.save(any(ServiceRequests.class))).thenReturn(savedRequests);
		when(serviceRequestsRepo.findByRequestId(req)).thenReturn(Optional.empty());

		UserDetails userDetails = detailsService.create(newUserDetails, req);

		assertEquals(savedUserDetails.getId(), userDetails.getId());
		assertEquals(savedUserDetails.getName(), userDetails.getName());
		assertEquals(savedUserDetails.getEmail(), userDetails.getEmail());


	}

	@Test
	public void testGetAll() {
		UserDetails userDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		UserDetails userDetails2 = new UserDetails(2L, "test-user2", "TEST-ROLE", "testuser2@gmail.com", "9876548219","testuser2",
				"IND", "MH", "Pune","0");

		when(detailsRepository.findAll()).thenReturn(Arrays.asList(userDetails, userDetails2));
		List<UserDetails> users = detailsService.getAll();
		assertEquals(2, users.size());
		assertEquals(users.get(0).getName(), "test-user");
		assertEquals(users.get(1).getName(), "test-user2");
	}

	@Test
	public void testGetAllWithPagination() {
		UserDetails userDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		UserDetails userDetails2 = new UserDetails(2L, "test-user2", "TEST-ROLE", "testuser2@gmail.com", "9876548219","testuser2",
				"IND", "MH", "Pune","0");
		List<UserDetails> users = new ArrayList<>(List.of(userDetails, userDetails2));

		Page<UserDetails> expPage = new PageImpl<>(users);

		PageRequest page = PageRequest.of(0, 2);
		when(detailsRepository.findAll(page)).thenReturn(expPage);

		Page<UserDetails> actPage = detailsService.getAllWithPagination(0, 2);
		assertEquals(expPage.getContent().size(), actPage.getContent().size());
		assertEquals(expPage.getContent().get(0).getCity(), actPage.getContent().get(0).getCity());
		assertEquals(expPage.getContent().get(1).getCity(), actPage.getContent().get(1).getCity());
		assertEquals(expPage.getContent().get(0).getName(), actPage.getContent().get(0).getName());
		assertEquals(expPage.getContent().get(1).getName(), actPage.getContent().get(1).getName());
	}

	@Test
	public void testGetByIdFromCache() {
		UserDetails userDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		when(detailsRepository.findById(1L)).thenReturn(Optional.of(userDetails));
		
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		
		when(redisService.get("user_id_1", UserDetails.class)).thenReturn(userDetails);
		
		UserDetails actUserDetails = detailsService.getById(1L);
		assertEquals(actUserDetails.getId(), userDetails.getId());
		assertEquals(actUserDetails.getCity(), userDetails.getCity());

	}
	
	@Test
	public void testGetById() {
		UserDetails userDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		when(detailsRepository.findById(1L)).thenReturn(Optional.of(userDetails));
		
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		
		when(redisService.get("user_id_1", UserDetails.class)).thenReturn(null);
		
		UserDetails actUserDetails = detailsService.getById(1L);
		assertEquals(actUserDetails.getId(), userDetails.getId());
		assertEquals(actUserDetails.getCity(), userDetails.getCity());

	}
	
	@Test
	public void testGetByIdException() {
	
		when(detailsRepository.findById(10000L)).thenReturn(Optional.empty());
		
		UserNotFoundException exception =	assertThrows(UserNotFoundException.class, ()->{
			detailsService.getById(10000L);
		});
		
	assertNotNull(exception);
	assertEquals("user is not present with this id", exception.getMessage());
		

	}
	

	@Test
	public void testUpdate() {
		UserDetails dbUserDetails = new UserDetails(1L,"test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		
		UserDetails updatedUserDetails = new UserDetails(1L, "test-updated-user", "TEST-ROLE", "testupdateduser@gmail.com", "9876543219","testuserupdated",
				"IND", "WB", "Kolkata","0");

		when(detailsRepository.findById(1L)).thenReturn(Optional.of(dbUserDetails));
		when(detailsRepository.save(dbUserDetails)).thenReturn(updatedUserDetails);
	

		UserDetails userDetails = detailsService.update(dbUserDetails);

		assertEquals(updatedUserDetails.getId(), userDetails.getId());
		assertEquals(updatedUserDetails.getName(), userDetails.getName());
		assertEquals(updatedUserDetails.getEmail(), userDetails.getEmail());
	}
	
	

	@Test
	public void testDelete() {
		
		UserDetails dbUserDetails = new UserDetails(1L,"test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata","0");
		
		ServiceRequests savedRequests = new ServiceRequests(1L, UUID.randomUUID().toString()
				, dbUserDetails,
				EntityEnum.USER.getEntityTypeId());
	
		when(detailsRepository.findById(1L)).thenReturn(Optional.of(dbUserDetails));

		when(serviceRequestsRepo.findByEntityId(dbUserDetails))
		           .thenReturn(Optional.of(savedRequests));
	
		UserDetails userDetails = detailsService.delete(1L);

		assertEquals(dbUserDetails.getId(), userDetails.getId());
		assertEquals(dbUserDetails.getName(), userDetails.getName());
		assertEquals(dbUserDetails.getEmail(), userDetails.getEmail());
		
		//verify the deletion 
		verify(serviceRequestsRepo).deleteById(savedRequests.getId());
		verify(detailsRepository).deleteById(userDetails.getId());
	}


}
