package com.bookingapi.bookingapi.model.dto.responsebody;

import com.bookingapi.bookingapi.enumerator.UserType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserResponseDTO(Long id, String email, UserType userType) {

}
