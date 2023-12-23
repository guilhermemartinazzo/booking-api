package com.bookingapi.bookingapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.model.dto.requestbody.UserDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.UserResponseDTO;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.service.UserService;

class UserControllerIntegrationTest extends TestController {

	@Autowired
	private UserService service;

	@Test
	@DisplayName("Create a user of type GUEST - Success")
	void createUserTest() throws Exception {
		// Preparing
		UserDTO user = new UserDTO("userguest@test.com", UserType.GUEST);
		// Executing
		MvcResult result = mockMvc.perform(post("/v1/user").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(user))).andExpect(status().isCreated()).andReturn();
		// Validating
		UserResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				UserResponseDTO.class);
		User userCreatedFromDataBase = service.findUserById(responseObtained.id());
		assertNotNull(userCreatedFromDataBase);
		assertEquals(userCreatedFromDataBase.getEmail(), user.email());
		assertEquals(userCreatedFromDataBase.getUserType(), user.userType());
	}

}
