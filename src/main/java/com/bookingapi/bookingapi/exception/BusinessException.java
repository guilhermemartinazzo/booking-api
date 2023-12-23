package com.bookingapi.bookingapi.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Getter
	private final HttpStatus httpStatus;

	public BusinessException(String message) {
		super(message);
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}

	public BusinessException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
