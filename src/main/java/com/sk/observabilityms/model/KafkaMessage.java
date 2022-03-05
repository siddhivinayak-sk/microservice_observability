package com.sk.observabilityms.model;

import java.io.Serializable;

public class KafkaMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	private String messageCode;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	
	@Override
	public String toString() {
		return "KafkaMessage [message=" + message + ", messageCode=" + messageCode + "]";
	}
}
