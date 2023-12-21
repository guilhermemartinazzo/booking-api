package com.bookingapi.bookingapi.model.dto.responsebody;

import lombok.Builder;

@Builder
public record PropertyResponseDTO(Long id, String description, String ownerEmail, String managerEmail,
		long activeBookings) {

}
