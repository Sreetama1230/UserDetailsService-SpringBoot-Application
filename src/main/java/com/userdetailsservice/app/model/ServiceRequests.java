package com.userdetailsservice.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ServiceRequests {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String requestId;
	@Column
	private long entityId;

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

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public long getEntityType() {
		return entityType;
	}

	public void setEntityType(long entityType) {
		this.entityType = entityType;
	}

	public ServiceRequests(String requestId, long entityId, long entityType) {
		super();
		this.requestId = requestId;
		this.entityId = entityId;
		this.entityType = entityType;
	}

	public ServiceRequests() {
		super();
	}

}
