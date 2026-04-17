package com.userdetailsservice.app.entityenum;

public enum EntityEnum {

	USER(1);

	private int entityTypeId;

	EntityEnum(int entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	public int getEntityTypeId() {
		return entityTypeId;
	}

}
