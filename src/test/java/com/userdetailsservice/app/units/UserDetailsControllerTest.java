package com.userdetailsservice.app.units;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userdetailsservice.app.controller.UserDetailsController;
import com.userdetailsservice.app.exp.UserNotFoundException;
import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.service.UserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserDetailsController.class)
public class UserDetailsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserDetailsService userDetailsService;

	private UserDetails userDetails;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		userDetails = new UserDetails(1L, "test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser", "IND", "WB",
				"Kolkata");

	}

	@Test
	public void testGetAll() throws Exception {
		UserDetails userDetails2 = new UserDetails(2L, "test-user2", "TEST-ROLE", "testuser2@gmail.com", "9876548219",
				"testuser2","IND", "MH", "Pune");
		List<UserDetails> users = Arrays.asList(userDetails, userDetails2);
		when(userDetailsService.getAll()).thenReturn(users);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/all")).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].name").value("test-user"))
				.andExpect(jsonPath("$[1].name").value("test-user2"));

	}

	@Test
	public void testGetAllWithPagination() throws Exception {
		UserDetails userDetails2 = new UserDetails(2L, "test-user2", "TEST-ROLE", "testuser2@gmail.com", "9876548219","testuser2",
				"IND", "MH", "Pune");
		List<UserDetails> users = Arrays.asList(userDetails, userDetails2);
		Page<UserDetails> pages = new PageImpl<>(users);
		when(userDetailsService.getAllWithPagination(0, 2)).thenReturn(pages);

		mockMvc.perform(MockMvcRequestBuilders.get("/user").param("pageNumber", "0").param("pageSize", "2"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].name").value("test-user"))
				.andExpect(jsonPath("$.content[1].name").value("test-user2"));

	}

	@Test
	public void testGetById() throws Exception {

		when(userDetailsService.getById(1)).thenReturn(userDetails);

		mockMvc.perform(MockMvcRequestBuilders.get("/user/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("test-user"));
	}

	@Test
	public void testGetByIdException() throws Exception {

		when(userDetailsService.getById(2)).thenThrow(new UserNotFoundException("user is not present with this id"));

		mockMvc.perform(
				MockMvcRequestBuilders.get("/user/2"))
		.andDo(print())
		.andExpect(status().isNotFound())
		
		.andExpect(jsonPath("$.name").value("UserNotFoundException"));
	}
	
	@Test
	public void testCreate() throws Exception {
		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata");
		String req = UUID.randomUUID().toString();
		when(userDetailsService.create(any(UserDetails.class), eq(req))).thenReturn(userDetails);

		mockMvc.perform(

				MockMvcRequestBuilders.post("/user").param("requestid", req)
						.content(objectMapper.writeValueAsString(newUserDetails))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.city").value("Kolkata"))
				.andExpect(jsonPath("$.name").value("test-user"))
				.andExpect(jsonPath("$.role").value("TEST-ROLE"));
	}
	
	@Test
	public void testCreateWithOutRequestId() throws Exception {
		UserDetails newUserDetails = new UserDetails("test-user", "TEST-ROLE", "testuser@gmail.com", "9876543219","testuser",
				"IND", "WB", "Kolkata");
	
		when(userDetailsService.create(any(UserDetails.class))).thenReturn(userDetails);

		mockMvc.perform(

				MockMvcRequestBuilders.post("/user")
						.content(objectMapper.writeValueAsString(newUserDetails))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.city").value("Kolkata"))
				.andExpect(jsonPath("$.name").value("test-user"))
				.andExpect(jsonPath("$.role").value("TEST-ROLE"));
	}
	
	@Test
	public void testUpdate() throws Exception {

		when(userDetailsService.update(any(UserDetails.class))).thenReturn(userDetails);

		mockMvc.perform(

				MockMvcRequestBuilders
				.post("/user")
					.content(objectMapper.writeValueAsString(userDetails))
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.city").value("Kolkata"))
				.andExpect(jsonPath("$.name").value("test-user"))
				.andExpect(jsonPath("$.role").value("TEST-ROLE"));
	}
	
	@Test
	public void testDelete() throws Exception {

		when(userDetailsService.delete(1L)).thenReturn(userDetails);

		mockMvc.perform(

				MockMvcRequestBuilders
				.delete("/user/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.city").value("Kolkata"))
				.andExpect(jsonPath("$.name").value("test-user"))
				.andExpect(jsonPath("$.role").value("TEST-ROLE"));
	}

}
