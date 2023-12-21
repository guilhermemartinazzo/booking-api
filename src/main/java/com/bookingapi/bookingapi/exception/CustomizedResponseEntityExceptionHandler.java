package com.bookingapi.bookingapi.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bookingapi.bookingapi.model.dto.responsebody.ExceptionResponseDTO;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponseDTO> handletAllExceptions(Exception ex, WebRequest request) {
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(ex.getMessage(), LocalDateTime.now(),
				request.getDescription(false));
		return ResponseEntity.internalServerError().body(exceptionResponse);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<ExceptionResponseDTO> handleResourceNotFoundException(Exception ex,
			WebRequest request) {
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(ex.getMessage(), LocalDateTime.now(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BusinessException.class)
	public final ResponseEntity<ExceptionResponseDTO> handleBusinessException(BusinessException ex,
			WebRequest request) {
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(ex.getMessage(), LocalDateTime.now(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<ExceptionResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex,
			WebRequest request) {
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(ex.getMessage(), LocalDateTime.now(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(ex.getLocalizedMessage(), LocalDateTime.now(),
				request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<String> listErrors = ex.getBindingResult().getAllErrors().stream().map(this::formatListObjectErrors)
				.toList();
		ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(StringUtils.join(listErrors, '|'),
				LocalDateTime.now(), request.getDescription(false));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	private String formatListObjectErrors(ObjectError objectError) {
		String defaultMessage = objectError.getDefaultMessage();
		String field = ((FieldError) objectError).getField();
		return String.format("%s: %s", field, defaultMessage);

	}

}
