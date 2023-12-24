package com.bookingapi.bookingapi.model.dto.requestbody;

import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.group.CreateGroup;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserDTO(@Email(groups = CreateGroup.class) String email,
		@NotNull(groups = CreateGroup.class) UserType userType) {

}
