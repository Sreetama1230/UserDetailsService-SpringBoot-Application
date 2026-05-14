package com.userdetailsservice.app.exp;

public class MissingOrInvalidSyncTokenException extends RuntimeException{

	public MissingOrInvalidSyncTokenException(String msg){
		super(msg);
	}
	public MissingOrInvalidSyncTokenException(){
		
	}
}
