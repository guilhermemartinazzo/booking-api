package com.bookingapi.bookingapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.model.dto.requestbody.UserDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.UserResponseDTO;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("Creating a user")
	void createUserTest() {
		UserDTO userDTO = UserDTO.builder().email("emailtest@test.com").userType(UserType.GUEST).build();
		User userExpected = User.builder().id(1L).email(userDTO.email()).userType(userDTO.userType()).build();
		Mockito.when(userRepository.save(any(User.class))).thenReturn(userExpected);
		UserResponseDTO obtained = service.createUser(userDTO);
		assertThat(obtained).hasFieldOrPropertyWithValue("id", userExpected.getId())
				.hasFieldOrPropertyWithValue("email", userExpected.getEmail())
				.hasFieldOrPropertyWithValue("userType", userExpected.getUserType());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("Find all users")
	void findAllUsersTest() {
		List<User> lstUsers = List.of(User.builder().email("email2@test.com").id(1L).build());
		Mockito.when(userRepository.findAll()).thenReturn(lstUsers);
		List<UserResponseDTO> obtained = service.findAllUsers();
		assertThat(obtained).hasSameSizeAs(lstUsers);
		verify(userRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Find a userDTO by id")
	void findUserDTOByIdTest() {
		User userExpected = User.builder().id(1L).userType(UserType.GUEST).email("email@test.com").build();
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExpected));
		UserResponseDTO obtained = service.findUserDTOById(1L);
		assertThat(obtained).hasFieldOrPropertyWithValue("id", userExpected.getId())
				.hasFieldOrPropertyWithValue("email", userExpected.getEmail())
				.hasFieldOrPropertyWithValue("userType", userExpected.getUserType());
		verify(userRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Find a user by id")
	void findUserByIdTest() {
		User userExpected = User.builder().id(1L).userType(UserType.GUEST).email("email@test.com").build();
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userExpected));
		User obtained = service.findUserById(1L);
		assertThat(obtained).isEqualTo(userExpected);
		verify(userRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Find a user by id - not found")
	void findUserByIdNotFoundTest() {
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> service.findUserById(1L));
		verify(userRepository, times(1)).findById(anyLong());
	}

}
