package com.bookingapi.bookingapi.model.dto.responsebody;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExceptionResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String message;
	private LocalDateTime timestamp;
	private String details;

}
