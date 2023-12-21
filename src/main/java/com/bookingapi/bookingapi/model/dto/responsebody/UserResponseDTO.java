package com.bookingapi.bookingapi.model.dto.responsebody;

import com.bookingapi.bookingapi.enumerator.UserType;

import lombok.Builder;

@Builder
public record UserResponseDTO(Long id, String email, UserType userType) {

}
