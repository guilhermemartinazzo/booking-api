package com.bookingapi.bookingapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookingapi.bookingapi.model.dto.requestbody.UserDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.UserResponseDTO;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;

	public List<UserResponseDTO> findAllUsers() {
		List<User> allUsers = repository.findAll();
		return allUsers.stream().map(this::buildUserResponseDTO).toList();
	}

	public UserResponseDTO createUser(UserDTO user) {
		User userEntity = User.builder().email(user.email()).userType(user.userType()).build();
		repository.save(userEntity);
		return buildUserResponseDTO(userEntity);
	}

	private UserResponseDTO buildUserResponseDTO(User userEntity) {
		return UserResponseDTO.builder().id(userEntity.getId()).email(userEntity.getEmail())
				.userType(userEntity.getUserType()).build();
	}

	public User findUserById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	public UserResponseDTO findUserDTOById(Long id) {
		return buildUserResponseDTO(findUserById(id));
	}
}
