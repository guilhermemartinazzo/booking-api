package com.bookingapi.bookingapi.model.dto.requestbody;

import java.time.LocalDate;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.util.BookingApiUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BlockDTO(@NotNull Long propertyId,
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) @FutureOrPresent(groups = CreateGroup.class) LocalDate startDate,
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) @Future(groups = CreateGroup.class) LocalDate endDate,
		@NotNull Long userId
		) {

}
