package com.userdetailsservice.app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userdetailsservice.app.model.UserDetails;
import com.userdetailsservice.app.service.UserDetailsService;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

	private UserDetailsService userDetailsService;

	public UserDetailsController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@PostMapping
	public ResponseEntity<UserDetails> create(@RequestBody UserDetails details, @RequestParam(defaultValue = "") String requestid) {
		// id has been provided so means it will be an update call
		if (details.getId() != null) {
			return new ResponseEntity<UserDetails>(userDetailsService.update(details), HttpStatus.OK);
		}
		if (requestid.equals("")) {
			UserDetails savedObject = userDetailsService.create(details);
			return new ResponseEntity<UserDetails>(savedObject, HttpStatus.CREATED);
		}
		
		UserDetails savedObject = userDetailsService.create(details,requestid);
		return new ResponseEntity<UserDetails>(savedObject, HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public ResponseEntity<List<UserDetails>> getAll() {
		return new ResponseEntity<List<UserDetails>>(userDetailsService.getAll(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDetails> getById( @PathVariable long id) {
		return new ResponseEntity<UserDetails>(userDetailsService.getById(id), HttpStatus.OK);
	}

	
	@GetMapping
	public ResponseEntity<Page<UserDetails>> getAllWithPagination(@RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize) {

		return new ResponseEntity<Page<UserDetails>>(userDetailsService.getAllWithPagination(pageNumber, pageSize),
				HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UserDetails> delete( @PathVariable long id) {
		return new ResponseEntity<UserDetails>(userDetailsService.delete(id), HttpStatus.OK);
	}

}
