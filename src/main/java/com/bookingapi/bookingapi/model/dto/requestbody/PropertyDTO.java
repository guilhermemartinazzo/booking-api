package com.bookingapi.bookingapi.model.dto.requestbody;

import lombok.Builder;

@Builder
public record PropertyDTO(Long id, String description, Long managerId, Long ownerId) {

}
