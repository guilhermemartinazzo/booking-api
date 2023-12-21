package com.bookingapi.bookingapi.model.dto.requestbody;

import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserDTO(@NotNull(groups = UpdateGroup.class) Long id,
		@Email(groups = CreateGroup.class) String email,
		@NotNull(groups = CreateGroup.class) UserType userType) {

}
