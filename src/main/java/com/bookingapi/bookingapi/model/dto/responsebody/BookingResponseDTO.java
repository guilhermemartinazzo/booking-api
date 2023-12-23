package com.bookingapi.bookingapi.model.dto.responsebody;

import java.time.LocalDate;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.util.BookingApiUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BookingResponseDTO(@NotNull(groups = {
		UpdateGroup.class }) Long id, String propertyDescription, String emailUser,
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) LocalDate startDate,
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) LocalDate endDate,
		BookingStatus status, String details){

}
