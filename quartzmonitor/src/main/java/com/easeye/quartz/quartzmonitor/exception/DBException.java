package com.easeye.quartz.quartzmonitor.exception;

public class DBException extends Exception {
	public DBException() {
		
	}
	
	public DBException(String message) {
		super(message);
	}
	
	public DBException(String message, Exception ex) {
		super(message, ex);
	}
}
