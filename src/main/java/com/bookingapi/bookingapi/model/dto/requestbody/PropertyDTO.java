package com.bookingapi.bookingapi.model.dto.requestbody;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PropertyDTO(@NotNull String description, @NotNull Long managerId, @NotNull Long ownerId) {

}
