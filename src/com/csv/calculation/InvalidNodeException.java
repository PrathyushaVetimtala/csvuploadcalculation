package com.csv.calculation;

public class InvalidNodeException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public String nodeKey;
	
	public InvalidNodeException(String message, String key) {
		super(message);
		this.nodeKey = key;
	}
	
	public InvalidNodeException(String message){
		super(message);
	}
}
