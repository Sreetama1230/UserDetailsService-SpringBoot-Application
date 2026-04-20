package com.userdetailsservice.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserDetails {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String name;
	
	@Column
	private String role;
	@Column
	private String email;
	@Column
	private String phoneNo;

	@Column
	private String username;
	
	@Column
	private String country;
	@Column
	private String state;
	@Column
	private String city;
	
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public UserDetails(Long id, String name, String role, String email, String phoneNo, String username, String country,
			String state, String city) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
		this.email = email;
		this.phoneNo = phoneNo;
		this.username = username;
		this.country = country;
		this.state = state;
		this.city = city;
	}

	public UserDetails(String name, String role, String email, String phoneNo, String username, String country,
			String state, String city) {
		super();
		this.name = name;
		this.role = role;
		this.email = email;
		this.phoneNo = phoneNo;
		this.username = username;
		this.country = country;
		this.state = state;
		this.city = city;
	}

	public UserDetails(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}


	
	
	
}
