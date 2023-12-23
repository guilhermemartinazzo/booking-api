package com.bookingapi.bookingapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.model.dto.requestbody.CreateBookingDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.model.entity.Booking;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.BookingRepository;
import com.bookingapi.bookingapi.service.validator.BookingValidatorService;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	@InjectMocks
	private BookingService service;

	@Spy
	private BookingRepository bookingRepository;
	@Mock
	private PropertyService propertyService;
	@Mock
	private UserService userService;
	@Mock
	private BookingValidatorService bookingValidator;

	@Test
	@DisplayName("Creating a booking - Sucess")
	void createBooking_Sucess() {
		// Preparing
		final Long idUserGuest = 1L;
		CreateBookingDTO bookingDTO = CreateBookingDTO.builder().userId(idUserGuest).build();
		User userGuest = buildUserEntity(idUserGuest, "emailtest@email.com", UserType.GUEST);
		Mockito.when(userService.findUserById(bookingDTO.userId())).thenReturn(userGuest);
		Property property = buildPropertyEntity(1L, "description", null, null);
		Mockito.when(propertyService.findById(bookingDTO.propertyId())).thenReturn(property);
		Booking bookingCreated = buildBookingEntity(1L, LocalDate.now(), LocalDate.now().plusDays(1),
				BookingStatus.ACTIVE, "Details", userGuest, property);
		Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(bookingCreated);
		// Executing
		BookingResponseDTO obtained = service.createBooking(bookingDTO);
		// Validating
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingCreated.getId())
				.hasFieldOrPropertyWithValue("status", bookingCreated.getStatus())
				.hasFieldOrPropertyWithValue("startDate", bookingCreated.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingCreated.getEndDate())
				.hasFieldOrPropertyWithValue("details", bookingCreated.getDetails())
				.hasFieldOrPropertyWithValue("emailUser", bookingCreated.getUser().getEmail());
		verify(bookingValidator, times(1)).validateUserInformedIsTypeGuest(userGuest);
		verify(bookingValidator, times(1)).validateUserInformedIsTypeGuest(userGuest);
		verify(bookingRepository, times(1)).save(any(Booking.class));
	}

	@Test
	@DisplayName("Deleting a booking - Sucess")
	void deleteBooking_Sucess() {
		Long bookingId = 1L;
		Long userId = 1L;
		Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(bookingId).build());
		Mockito.when(bookingRepository.findById(bookingId)).thenReturn(bookingOptional);
		service.delete(bookingId, userId);
		verify(bookingValidator, times(1)).validateUserCanUpdateBooking(userId, bookingOptional.get());
		verify(bookingRepository, times(1)).delete(any(Booking.class));
	}
	
	@Test
	@DisplayName("Update a booking - Sucess")
	void updateBooking_Sucess() {
		Long bookingId = 1L;
		Long userId = 1L;
		Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(bookingId).build());
		Mockito.when(bookingRepository.findById(bookingId)).thenReturn(bookingOptional);
		service.delete(bookingId, userId);
		verify(bookingValidator, times(1)).validateUserCanUpdateBooking(userId, bookingOptional.get());
		verify(bookingRepository, times(1)).delete(any(Booking.class));
	}

	private Booking buildBookingEntity(Long id, LocalDate startDate, LocalDate endDate, BookingStatus status,
			String details, User user, Property property) {
		return Booking.builder().id(id).details(details).status(status).user(user).property(property)
				.startDate(startDate).endDate(endDate).build();
	}

	private Property buildPropertyEntity(Long id, String description, User manager, User owner) {
		Property prop = new Property();
		prop.setDescription(description);
		prop.setId(id);
		prop.setManager(manager);
		prop.setOwner(owner);
		return prop;
	}

	private User buildUserEntity(final Long id, String email, UserType type) {
		return User.builder().id(id).email(email).userType(type).build();

	}
}
