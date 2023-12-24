package com.bookingapi.bookingapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.CreateBookingDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.RebookingCanceledDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.UpdateBookingDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.model.entity.Booking;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.BookingRepository;
import com.bookingapi.bookingapi.service.validator.BookingValidatorService;

import jakarta.persistence.EntityNotFoundException;

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
	@DisplayName("Creating a booking")
	void createBooking() {
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
	@DisplayName("Deleting a booking")
	void deleteBooking() {
		// Preparing
		Long bookingId = 1L;
		Long userId = 1L;
		Optional<Booking> bookingOptional = Optional.of(Booking.builder().id(bookingId).build());
		Mockito.when(bookingRepository.findById(bookingId)).thenReturn(bookingOptional);
		// Executing
		service.delete(bookingId, userId);
		// Validating
		verify(bookingValidator, times(1)).validateUserCanUpdateBooking(userId, bookingOptional.get());
		verify(bookingRepository, times(1)).delete(any(Booking.class));
	}

	@Test
	@DisplayName("Updates a booking")
	void updateBooking() {
		// Preparing
		Long bookingId = 1L;
		Long userId = 1L;
		UpdateBookingDTO updateBookingDTO = UpdateBookingDTO.builder().userId(userId)
				.startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(2)).build();
		Optional<Booking> bookingOptional = Optional
				.of(Booking.builder().id(bookingId).property(buildPropertyEntity(1L, "", null, null)).build());
		Mockito.when(bookingRepository.findById(bookingId)).thenReturn(bookingOptional);
		Mockito.when(userService.findUserById(userId)).thenReturn(buildUserEntity(userId, null, null));
		Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(bookingOptional.get());
		// Executing
		BookingResponseDTO obtained = service.update(bookingId, updateBookingDTO);
		// Validating
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingOptional.get().getId());
		verify(bookingValidator, times(1)).validateUserCanUpdateBooking(userId, bookingOptional.get());
		verify(bookingValidator, times(1)).validateBookingPersistence(userId, updateBookingDTO.startDate(),
				updateBookingDTO.endDate(), bookingOptional.get().getProperty().getId());
		verify(bookingRepository, times(1)).save(any(Booking.class));
	}

	@Test
	@DisplayName("Creates a block")
	void createBlock() {
		// Preparing
		Long userId = 1L;
		Long propertyId = 1L;
		BlockDTO blockDTO = BlockDTO.builder().userId(userId).propertyId(propertyId).build();
		User userEntity = buildUserEntity(userId, "emailUser@test.com", UserType.MANAGER);
		Mockito.when(userService.findUserById(userId)).thenReturn(userEntity);
		Property property = buildPropertyEntity(propertyId, null, null, null);
		Mockito.when(propertyService.findById(userId)).thenReturn(property);
		Booking bookingEntity = buildBookingEntity(1L, blockDTO.startDate(), blockDTO.endDate(), BookingStatus.BLOCKED,
				"details", userEntity, property);
		Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(bookingEntity);
		// Executing
		BookingResponseDTO obtained = service.createBlock(blockDTO);
		// Validating
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingEntity.getId())
				.hasFieldOrPropertyWithValue("status", BookingStatus.BLOCKED)
				.hasFieldOrPropertyWithValue("startDate", bookingEntity.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingEntity.getEndDate());
		verify(bookingRepository, times(1)).save(any(Booking.class));
		verify(bookingValidator, times(1)).validateBookingDates(blockDTO.startDate(), blockDTO.endDate());
		verify(bookingValidator, times(1)).validateUserHasPermissionToBlock(any(User.class), any(Property.class));
		verify(bookingValidator, times(1)).validatePropertyHasActiveOrBlockedBookings(blockDTO);
		verify(bookingValidator, times(1)).verifyPropertyHasCanceledBookings(blockDTO);
	}

	@Test
	@DisplayName("Updates a block")
	void updateBlock() {
		// Preparing
		Long userId = 1L;
		Long propertyId = 1L;
		BlockDTO blockDTO = BlockDTO.builder().userId(userId).propertyId(propertyId).build();
		User userEntity = buildUserEntity(userId, "emailUser@test.com", UserType.MANAGER);
		Mockito.when(userService.findUserById(userId)).thenReturn(userEntity);
		Property property = buildPropertyEntity(propertyId, null, null, null);
		Booking bookingEntity = buildBookingEntity(1L, blockDTO.startDate(), blockDTO.endDate(), BookingStatus.BLOCKED,
				"details", userEntity, property);
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
		Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(bookingEntity);
		// Executing
		BookingResponseDTO obtained = service.updateBlock(bookingEntity.getId(), blockDTO);
		// Validating
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingEntity.getId())
				.hasFieldOrPropertyWithValue("status", BookingStatus.BLOCKED)
				.hasFieldOrPropertyWithValue("startDate", bookingEntity.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingEntity.getEndDate());
		verify(bookingRepository, times(1)).save(any(Booking.class));
		verify(bookingValidator, times(1)).validateUserHasPermissionToBlock(userEntity, property);
		verify(bookingValidator, times(1)).validatePropertyHasActiveOrBlockedBookings(blockDTO);
		verify(bookingValidator, times(1)).verifyPropertyHasCanceledBookings(blockDTO);
	}

	@Test
	@DisplayName("Deleting a block")
	void deleteBlocking() {
		// Preparing
		Long bookingId = 1L;
		Long userId = 1L;
		User userEntity = buildUserEntity(userId, "emailUser@test.com", UserType.MANAGER);
		Optional<Booking> bookingOptional = Optional
				.of(Booking.builder().id(bookingId).property(new Property()).build());
		Mockito.when(bookingRepository.findById(bookingId)).thenReturn(bookingOptional);
		Mockito.when(userService.findUserById(userId)).thenReturn(userEntity);
		// Executing
		service.deleteBlock(bookingId, userId);
		// Validating
		verify(bookingValidator, times(1)).validateUserHasPermissionToBlock(any(User.class), any(Property.class));
		verify(bookingValidator, times(1)).validateBookingIsBlocked(any(Booking.class));
		verify(bookingRepository, times(1)).delete(any(Booking.class));
	}

	@Test
	@DisplayName("Find a bookingDTO by id")
	void findBookingDTOById() {
		Booking bookingEntity = buildBookingEntity(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
				BookingStatus.ACTIVE, "", buildUserEntity(1L, "email@test.com", UserType.GUEST),
				buildPropertyEntity(1L, "description", null, null));
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
		BookingResponseDTO obtained = service.findBookingResponseDTOById(anyLong());
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingEntity.getId())
				.hasFieldOrPropertyWithValue("startDate", bookingEntity.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingEntity.getEndDate())
				.hasFieldOrPropertyWithValue("status", bookingEntity.getStatus())
				.hasFieldOrPropertyWithValue("details", bookingEntity.getDetails())
				.hasFieldOrPropertyWithValue("propertyDescription", bookingEntity.getProperty().getDescription())
				.hasFieldOrPropertyWithValue("emailUser", bookingEntity.getUser().getEmail());
		;
		verify(bookingRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Find a book by id")
	void findBookingById() {
		Booking bookingEntity = buildBookingEntity(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
				BookingStatus.ACTIVE, "", buildUserEntity(1L, "email@test.com", UserType.GUEST),
				buildPropertyEntity(1L, "description", null, null));
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
		Booking obtained = service.findBookingById(anyLong());
		assertThat(obtained).isEqualTo(bookingEntity);
		verify(bookingRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Find a book by id - Not found")
	void findBookingById_NotFound() {
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> service.findBookingById(1L));
		verify(bookingRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Cancelling a booking")
	void cancellingBooking() {
		Booking bookingEntity = buildBookingEntity(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
				BookingStatus.ACTIVE, "", buildUserEntity(1L, "email@test.com", UserType.GUEST),
				buildPropertyEntity(1L, "description", null, null));
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
		Booking bookingExpected = bookingEntity;
		bookingExpected.setStatus(BookingStatus.CANCELED);
		Mockito.when(bookingRepository.save(bookingEntity)).thenReturn(bookingExpected);
		BookingResponseDTO obtained = service.cancelBooking(bookingEntity.getId(), 1L);
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingExpected.getId())
				.hasFieldOrPropertyWithValue("startDate", bookingExpected.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingExpected.getEndDate())
				.hasFieldOrPropertyWithValue("status", bookingExpected.getStatus())
				.hasFieldOrPropertyWithValue("details", bookingExpected.getDetails())
				.hasFieldOrPropertyWithValue("propertyDescription", bookingExpected.getProperty().getDescription())
				.hasFieldOrPropertyWithValue("emailUser", bookingExpected.getUser().getEmail());
		;
		verify(bookingRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Rebooking a canceled booking")
	void rebookCanceledBooking() {
		Booking bookingEntity = buildBookingEntity(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
				BookingStatus.ACTIVE, "", buildUserEntity(1L, "email@test.com", UserType.GUEST),
				buildPropertyEntity(1L, "description", null, null));
		Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntity));
		Booking bookingExpected = bookingEntity;
		RebookingCanceledDTO rebookingDTO = RebookingCanceledDTO.builder().details("details")
				.startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(2)).build();
		bookingExpected.setStatus(BookingStatus.ACTIVE);
		bookingExpected.setDetails(rebookingDTO.details());
		bookingExpected.setStartDate(rebookingDTO.startDate());
		bookingExpected.setEndDate(rebookingDTO.endDate());
		Mockito.when(bookingRepository.save(bookingEntity)).thenReturn(bookingExpected);
		BookingResponseDTO obtained = service.rebookCanceledBooking(bookingEntity.getId(), rebookingDTO);
		assertThat(obtained).hasFieldOrPropertyWithValue("id", bookingExpected.getId())
				.hasFieldOrPropertyWithValue("startDate", bookingExpected.getStartDate())
				.hasFieldOrPropertyWithValue("endDate", bookingExpected.getEndDate())
				.hasFieldOrPropertyWithValue("status", bookingExpected.getStatus())
				.hasFieldOrPropertyWithValue("details", bookingExpected.getDetails())
				.hasFieldOrPropertyWithValue("propertyDescription", bookingExpected.getProperty().getDescription())
				.hasFieldOrPropertyWithValue("emailUser", bookingExpected.getUser().getEmail());
		;
		verify(bookingRepository, times(1)).findById(anyLong());
		verify(bookingRepository, times(1)).save(any(Booking.class));
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
