package com.bookingapi.bookingapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.model.dto.requestbody.UserDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.UserResponseDTO;
import com.bookingapi.bookingapi.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping("/v1/user")
@RestController
@RequiredArgsConstructor
@Tag(name = "User Services")
public class UserController {

	private final UserService service;

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
		List<UserResponseDTO> users = service.findAllUsers();
		return ResponseEntity.ok(users);
	}

	@PostMapping
	public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Validated(CreateGroup.class) UserDTO user) {
		return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
	}

}
