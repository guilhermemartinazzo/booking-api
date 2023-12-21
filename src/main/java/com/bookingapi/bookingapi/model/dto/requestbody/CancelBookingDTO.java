package com.bookingapi.bookingapi.model.dto.requestbody;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CancelBookingDTO(@NotNull Long userId, @NotNull Long bookingId) {

}
