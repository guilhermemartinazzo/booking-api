package com.bookingapi.bookingapi.model.dto.responsebody;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PropertyResponseDTO(Long id, String description, String ownerEmail, String managerEmail,
		long activeBookings) {

}
