package com.bookingapi.bookingapi.model.dto.requestbody;

import java.time.LocalDate;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.util.BookingApiUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RebookingCanceledDTO(@NotNull(groups = {
		CreateGroup.class,
		UpdateGroup.class }) @JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) @FutureOrPresent(groups = CreateGroup.class) LocalDate startDate,

		@NotNull(groups = { CreateGroup.class,
				UpdateGroup.class }) @JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) @Future(groups = CreateGroup.class) LocalDate endDate,

		@NotNull(groups = { UpdateGroup.class, CreateGroup.class }) Long userId,

		String details){
}
