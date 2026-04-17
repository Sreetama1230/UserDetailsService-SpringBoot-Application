package com.userdetailsservice.app.exp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.userdetailsservice.app.model.ExceptionObject;
import com.userdetailsservice.app.model.UserNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<ExceptionObject> handleInvalidIdException(InvalidIdException  exception) {
		
		return new ResponseEntity<>(
				new ExceptionObject("InvalidIdException", HttpStatus.BAD_REQUEST.toString()
						, exception.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<ExceptionObject> handleUserNotFoundException(UserNotFoundException exception){
		return new ResponseEntity<>(
				new ExceptionObject("UserNotFoundException", HttpStatus.NOT_FOUND.toString()
						, exception.getMessage()), HttpStatus.NOT_FOUND);
	}

}
