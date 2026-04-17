package com.userdetailsservice.app.exp;

public class InvalidIdException extends RuntimeException{
	
	public InvalidIdException(){
		super();
	}
	public InvalidIdException(String message){
		super(message);
	}
}
