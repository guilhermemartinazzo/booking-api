package com.bookingapi.bookingapi.model.dto.requestbody;

import java.time.LocalDate;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.util.BookingApiUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookingDTO(Long id,
		@NotNull(groups = {CreateGroup.class, UpdateGroup.class}) 
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) 
		@FutureOrPresent(groups = CreateGroup.class) 
		LocalDate startDate,

		@NotNull(groups = {CreateGroup.class, UpdateGroup.class}) 
		@JsonFormat(shape = Shape.STRING, pattern = BookingApiUtils.DATE_FORMAT) 
		@Future(groups = CreateGroup.class) 
		LocalDate endDate,
		
		@NotNull(groups = {UpdateGroup.class, CreateGroup.class }) 
		Long propertyId,
		
		@NotNull(groups = UpdateGroup.class) 
		BookingStatus status,
		
		@NotNull(groups = { UpdateGroup.class, CreateGroup.class }) 
		Long userId){

}
