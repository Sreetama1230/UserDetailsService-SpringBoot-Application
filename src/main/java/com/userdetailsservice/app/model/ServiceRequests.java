package com.userdetailsservice.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ServiceRequests {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique=true)
	private String requestId;
	
	@ManyToOne
	@JoinColumn(name="entity_id")
	private UserDetails entityId;

	@Column
	private long entityType;

	public Long getId() {
		return id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getEntityType() {
		return entityType;
	}

	public void setEntityType(long entityType) {
		this.entityType = entityType;
	}



	public UserDetails getEntityId() {
		return entityId;
	}

	public void setEntityId(UserDetails entityId) {
		this.entityId = entityId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ServiceRequests() {
		super();
	}

	public ServiceRequests(String requestId, UserDetails entityId, long entityType) {
		super();
		this.requestId = requestId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

	public ServiceRequests(Long id, String requestId, UserDetails entityId, long entityType) {
		super();
		this.id = id;
		this.requestId = requestId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

}
