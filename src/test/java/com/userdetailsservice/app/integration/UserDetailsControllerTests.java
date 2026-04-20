package com.userdetailsservice.app.integration;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.repo.ServiceRequestsRepo;
import com.userdetailsservice.app.repo.UserDetailsRepository;
import com.userdetailsservice.app.service.UserDetailsService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserDetailsControllerTests {

	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private ServiceRequestsRepo serviceRequestsRepo;
	
	@Autowired
	private UserDetailsService userDetailsService;

	private static HttpHeaders headers;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeAll
	public static void init() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	
	@AfterEach
	void cleanUp() {
	    serviceRequestsRepo.deleteAll();
	    userDetailsRepository.deleteAll();
	}

	private String createURLWithPort() {
		return "http://localhost:" + port + "/user";
	}
	


	// get all
	@Test
	@Sql(statements = "INSERT INTO user_details(id,city,country,email,name,phone_no,username,role,state) VALUES (10009,'Pune','IND','priya@gmaail.com','Priya','9876541235','priya1234','ADMIN','MH')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//	@Sql(statements = "DELETE FROM user_details WHERE id='10009'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetAll() {
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<List<UserDetails>> resp = testRestTemplate.exchange(createURLWithPort() + "/all", HttpMethod.GET,
				entity, new ParameterizedTypeReference<List<UserDetails>>() {
				});

		List<UserDetails> details = resp.getBody();

		assert details != null;
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(details.size(), userDetailsService.getAll().size());
		assertEquals(details.size(), userDetailsRepository.findAll().size());

	}

	// pagination
	@Test
	@Sql(statements = "INSERT INTO user_details(id,city,country,email,name,phone_no,username,role,state) VALUES (10009,'Pune','IND','priya@gmaail.com','Priya','9876541235','priya1234','ADMIN','MH')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//	@Sql(statements = "DELETE FROM user_details WHERE id='10009'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetAllByPagination() {
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<String> resp = 
				testRestTemplate.exchange(createURLWithPort()+"?pageNumber=0&pageSize=2", HttpMethod.GET, entity,
				String.class);

		assertEquals(HttpStatus.OK, resp.getStatusCode());
	
		String body = resp.getBody();
		assertNotNull(body.contains("content"));
		assertTrue(body.contains("Priya"));
		assertTrue(body.contains("totalElement"));

	}

	@Test
	@Sql(statements = "INSERT INTO user_details(id,city,country,email,name,phone_no,username,role,state) "
			+ "VALUES (100066,'Pune','IND','priya@gmail.com','Priya','9876541235','priya1234','ADMIN','MH')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//	@Sql(statements = "DELETE FROM user_details WHERE id='100066'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetById() throws JsonProcessingException {
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<UserDetails> resp = testRestTemplate.exchange(createURLWithPort() + "/100066", HttpMethod.GET,
				entity, UserDetails.class);

		UserDetails details = resp.getBody();
		String expected = "{\"id\":100066,\"name\":\"Priya\",\"role\":\"ADMIN\",\"email\":\"priya@gmail.com\",\"phoneNo\":\"9876541235\",\"username\":\"priya1234\",\"country\":\"IND\",\"state\":\"MH\",\"city\":\"Pune\"}";

		assertEquals(expected, objectMapper.writeValueAsString(details));
		assert details != null;
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(details.getCity(), userDetailsService.getById(100066).getCity());
		assertEquals(details.getCity(), userDetailsRepository.findById(100066L).orElse(null).getCity());

	}

	@Test
//	@Sql(statements = "DELETE FROM user_details WHERE phone_no='9876543219'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testCreate() throws JsonProcessingException {

		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219",
				"testuser", "IND", "WB", "Kolkata");

		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(newUserDetails), headers);

		ResponseEntity<UserDetails> resp = testRestTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity,
				UserDetails.class);

		UserDetails details = resp.getBody();

		assertNotNull(details);
		assertEquals(HttpStatus.CREATED, resp.getStatusCode());
		assertNotNull(details.getId());
		assertEquals(details.getCity(), "Kolkata");
		assertEquals(details.getCity(), userDetailsRepository.save(newUserDetails).getCity());

	}

	@Test
//	@Sql(statements = "DELETE FROM user_details WHERE phone_no='9876543219'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testCreateWithRequestId() throws JsonProcessingException {

		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219",
				"testuser", "IND", "WB", "Kolkata");

		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(newUserDetails), headers);

		String reqId= UUID.randomUUID().toString();
		ResponseEntity<UserDetails> resp = testRestTemplate.exchange(createURLWithPort()+"?requestid="+reqId, HttpMethod.POST, entity,
				UserDetails.class);

		UserDetails details = resp.getBody();

		assertNotNull(details);
		assertEquals(HttpStatus.CREATED, resp.getStatusCode());
		assertNotNull(serviceRequestsRepo.findByRequestId(reqId));
		assertNotNull(details.getId());
		assertEquals(details.getCity(), "Kolkata");
		assertEquals(details.getCity(), userDetailsRepository.save(newUserDetails).getCity());

	}
	@Test
//	@Sql(statements = "DELETE FROM user_details WHERE phone_no='9865487651'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testUpdate() throws JsonProcessingException {

		// first create

		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219",
				"testuser", "IND", "WB", "Kolkata");

		HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(newUserDetails), headers);

		ResponseEntity<UserDetails> resp = testRestTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity,
				UserDetails.class);

		UserDetails details = resp.getBody();

		// update

		UserDetails updatedUserDetails = new UserDetails(details.getId(), "testuserupdated");

		entity = new HttpEntity<>(objectMapper.writeValueAsString(updatedUserDetails), headers);
		resp = testRestTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, UserDetails.class);

		details = resp.getBody();

		assertNotNull(details);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(details.getName(), "testuserupdated");
		assertEquals(details.getName(), userDetailsRepository.save(updatedUserDetails).getName());

	}

	@Test
	@Sql(statements = "INSERT INTO user_details(id,city,country,email,name,phone_no,username,role,state) "
			+ "VALUES (100066,'Pune','IND','priya@gmail.com','Priya','9876541235','priya1234','ADMIN','MH')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//	@Sql(statements = "DELETE FROM user_details WHERE id='100066'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testDelete() {
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<UserDetails> resp = testRestTemplate.exchange(createURLWithPort() + "/100066", HttpMethod.DELETE,
				entity, UserDetails.class);

		UserDetails details = resp.getBody();
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(details);

	}
}
