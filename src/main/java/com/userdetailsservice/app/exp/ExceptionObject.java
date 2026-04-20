package com.userdetailsservice.app.exp;

public class ExceptionObject {

	private String name;
	private String code;
	private String message;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ExceptionObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ExceptionObject(String name, String code, String message) {
		super();
		this.name = name;
		this.code = code;
		this.message = message;
	}
	
	
	

	
}
