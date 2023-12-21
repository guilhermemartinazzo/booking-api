package com.bookingapi.bookingapi.model.dto.responsebody;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;
	private LocalDateTime timestamp;
	private String details;

}
