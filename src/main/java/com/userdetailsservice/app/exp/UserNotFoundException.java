package com.userdetailsservice.app.exp;

public class UserNotFoundException  extends RuntimeException{
	
	public UserNotFoundException(){
		super();
	}
	public UserNotFoundException(String message) {
		super(message);
	}
}
