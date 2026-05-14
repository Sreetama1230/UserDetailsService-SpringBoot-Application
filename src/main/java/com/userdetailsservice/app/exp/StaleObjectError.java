package com.userdetailsservice.app.exp;

public class StaleObjectError extends RuntimeException{
	
	public StaleObjectError(String msg) {
		super(msg);
	}
	
	public StaleObjectError() {
		super();
	}

}
