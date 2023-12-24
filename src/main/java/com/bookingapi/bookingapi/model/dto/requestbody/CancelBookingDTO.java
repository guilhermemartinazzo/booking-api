package com.bookingapi.bookingapi.model.dto.requestbody;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CancelBookingDTO(@NotNull Long userId, @NotNull Long bookingId) {

}
