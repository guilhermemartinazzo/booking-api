package com.bookingapi.bookingapi.service.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.exception.BusinessException;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.entity.Booking;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingValidatorServiceTest {

	@Mock
	private BookingRepository bookingRepository;

	@InjectMocks
	private BookingValidatorService validatorService;

	@Test
	@DisplayName("Validates if user informed is of type Guest - Valid")
	void validateUserInformedIsTypeGuestTest() {
		User user = User.builder().userType(UserType.GUEST).build();
		assertDoesNotThrow(() -> validatorService.validateUserInformedIsTypeGuest(user));
	}

	@Test
	@DisplayName("Validates if user informed is of type Guest - Invalid")
	void validateUserInformedIsTypeGuestInvalidTest() {
		User user = User.builder().userType(UserType.MANAGER).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateUserInformedIsTypeGuest(user));
		assertEquals(BookingValidatorService.MSG_VALIDATE_USER_INFORMED_IS_TYPE_GUEST, exception.getMessage());
	}

	@Test
	@DisplayName("Validates booking persistence - Valid")
	void validateBookingPersistenceValidTest() {
		Long bookingId = 1L;
		LocalDate startDate = LocalDate.now().plusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(2);
		Long propertyId = 1L;
		List<Booking> lstBlockings = new ArrayList<>();
		Mockito.when(bookingRepository.findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate))
				.thenReturn(lstBlockings);
		assertDoesNotThrow(
				() -> validatorService.validateBookingPersistence(bookingId, startDate, endDate, propertyId));
		verify(bookingRepository, times(1)).findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate);
	}

	@Test
	@DisplayName("Validates booking persistence - Not Valid: With a block")
	void validateBookingPersistenceNotValidWithBlockTest() {
		Long bookingId = 1L;
		LocalDate startDate = LocalDate.now().plusDays(1);
		LocalDate endDate = LocalDate.now().plusDays(2);
		Long propertyId = 1L;
		List<Booking> lstBlockings = List.of(Booking.builder().id(2L).build());
		Mockito.when(bookingRepository.findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate))
				.thenReturn(lstBlockings);
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingPersistence(bookingId, startDate, endDate, propertyId));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BLOCKS_FROM_PROPERTY, exception.getMessage());
		verify(bookingRepository, times(1)).findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate);
	}

	@Test
	@DisplayName("Validates booking persistence - Not Valid: With invalid dates")
	void validateBookingPersistenceNotValidWithInvalidDatesTest() {
		Long bookingId = 1L;
		LocalDate startDate = LocalDate.now().plusDays(2);
		LocalDate endDate = LocalDate.now().plusDays(1);
		Long propertyId = 1L;
		List<Booking> lstBlockings = List.of(Booking.builder().id(1L).build());
		Mockito.when(bookingRepository.findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate))
				.thenReturn(lstBlockings);
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingPersistence(bookingId, startDate, endDate, propertyId));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BOOKING_DATES, exception.getMessage());
		verify(bookingRepository, times(1)).findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate);
	}

	@Test
	@DisplayName("Validates if a booking is with status canceled")
	void validateBookingIsAlreadyCanceledTest() {
		Booking booking = Booking.builder().status(BookingStatus.ACTIVE).build();
		assertDoesNotThrow(() -> validatorService.validateBookingIsAlreadyCanceled(booking));
	}

	@Test
	@DisplayName("Validates if a booking is with status canceled - Invalid")
	void validateBookingIsAlreadyCanceledInvalidTest() {
		Booking booking = Booking.builder().status(BookingStatus.CANCELED).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingIsAlreadyCanceled(booking));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BOOKING_IS_ALREADY_CANCELED, exception.getMessage());
	}

	@Test
	@DisplayName("Validates booking must be canceled to rebook")
	void validateBookingMustBeCanceledToRebookTest() {
		Booking booking = Booking.builder().status(BookingStatus.CANCELED).build();
		assertDoesNotThrow(() -> validatorService.validateBookingMustBeCanceledToRebook(booking));
	}

	@Test
	@DisplayName("Validates booking must be canceled to rebook - Invalid")
	void validateBookingMustBeCanceledToRebookInvalidTest() {
		Booking booking = Booking.builder().status(BookingStatus.ACTIVE).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingMustBeCanceledToRebook(booking));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BOOKING_MUST_BE_CANCELED_TO_REBOOK, exception.getMessage());
	}

	@Test
	@DisplayName("Validates if it's changing the booking status to blocked")
	void validateBookingStatusChangingToBlockedTest() {
		BookingStatus status = BookingStatus.ACTIVE;
		Booking booking = Booking.builder().status(BookingStatus.ACTIVE).build();
		assertDoesNotThrow(() -> validatorService.validateBookingStatusChangingToBlocked(status, booking));
	}

	@Test
	@DisplayName("Validates if it's changing the booking status to blocked - Invalid")
	void validateBookingStatusChangingToBlockedInvalidTest() {
		BookingStatus status = BookingStatus.BLOCKED;
		Booking booking = Booking.builder().status(BookingStatus.ACTIVE).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingStatusChangingToBlocked(status, booking));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BOOKING_STATUS_CHANGING_TO_BLOCKED, exception.getMessage());
	}

	@Test
	@DisplayName("Validates if a user has permission to block")
	void validateUserHasPermissionToBlockTest() {
		User userOwnerFromProperty = User.builder().id(1L).build();
		User userOwnerManagerProperty = User.builder().id(2L).build();
		Property property = Property.builder().owner(userOwnerFromProperty).manager(userOwnerManagerProperty).build();
		User userWithPermissionToBlock = userOwnerFromProperty;
		assertDoesNotThrow(
				() -> validatorService.validateUserHasPermissionToBlock(userWithPermissionToBlock, property));
	}

	@Test
	@DisplayName("Validates if a user has permission to block - ")
	void validateUserHasPermissionToBlockInvalidTest() {
		User userOwnerFromProperty = User.builder().id(1L).build();
		User userOwnerManagerProperty = User.builder().id(2L).build();
		Property property = Property.builder().owner(userOwnerFromProperty).manager(userOwnerManagerProperty).build();
		User userWithoutPermissionToBlock = User.builder().id(3L).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateUserHasPermissionToBlock(userWithoutPermissionToBlock, property));
		assertEquals(BookingValidatorService.MSG_VALIDATE_USER_HAS_PERMISSION_TO_BLOCK, exception.getMessage());
	}

	@Test
	@DisplayName("Validates if the property has active or blocked booking")
	void validatePropertyHasActiveOrBlockedBookingsTest() {
		BlockDTO blockDTO = BlockDTO.builder().startDate(LocalDate.now().plusDays(1))
				.endDate(LocalDate.now().plusDays(2)).build();
		assertDoesNotThrow(() -> validatorService.validatePropertyHasActiveOrBlockedBookings(blockDTO));
	}

	@Test
	@DisplayName("Validates if the property has active or blocked booking- Invalid")
	void validatePropertyHasActiveOrBlockedBookingsInvalidTest() {
		List<Booking> lstBlockings = List.of(Booking.builder().id(2L).build());
		Mockito.when(bookingRepository.findBookingsFromPropertyBetweenDates(anyLong(), any(LocalDate.class),
				any(LocalDate.class), anyList())).thenReturn(lstBlockings);
		BlockDTO blockDTO = BlockDTO.builder().startDate(LocalDate.now().plusDays(1)).propertyId(1L)
				.endDate(LocalDate.now().plusDays(2)).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validatePropertyHasActiveOrBlockedBookings(blockDTO));
		assertEquals(BookingValidatorService.MSG_VALIDATE_PROPERTY_HAS_ACTIVE_OR_BLOCKED_BOOKINGS,
				exception.getMessage());
	}

	@Test
	@DisplayName("Validates if the property has canceled booking")
	void validatePropertyHasCanceledBookingsTest() {
		BlockDTO blockDTO = BlockDTO.builder().startDate(LocalDate.now().plusDays(1)).propertyId(1L)
				.endDate(LocalDate.now().plusDays(2)).build();
		assertDoesNotThrow(() -> validatorService.verifyPropertyHasCanceledBookings(blockDTO));
		verify(bookingRepository, times(0)).deleteAll(anyList());
	}

	@Test
	@DisplayName("Validates if the property has canceled booking")
	void validatePropertyHasCanceledBookingsInvalidTest() {
		BlockDTO blockDTO = BlockDTO.builder().startDate(LocalDate.now().plusDays(1)).propertyId(1L)
				.endDate(LocalDate.now().plusDays(2)).build();
		List<Booking> lstBlockings = List.of(Booking.builder().id(2L).build());
		Mockito.when(bookingRepository.findBookingsFromPropertyBetweenDates(anyLong(), any(LocalDate.class),
				any(LocalDate.class), anyList())).thenReturn(lstBlockings);
		assertDoesNotThrow(() -> validatorService.verifyPropertyHasCanceledBookings(blockDTO));
		verify(bookingRepository, times(1)).deleteAll(lstBlockings);
	}

	@Test
	@DisplayName("Validates if user can update a booking - User owner of property")
	void validateUserCanUpdateBokingOwnerTest() {
		User userWithPermission = User.builder().id(1L).build();
		Property property = Property.builder().owner(userWithPermission).manager(User.builder().id(3L).build()).build();
		Booking booking = Booking.builder().property(property).user(User.builder().build()).build();
		assertDoesNotThrow(() -> validatorService.validateUserCanUpdateBooking(userWithPermission.getId(), booking));
	}

	@Test
	@DisplayName("Validates if user can update a booking - User manager and owner of property")
	void validateUserCanUpdateBokingManagerTest() {
		User userWithPermission = User.builder().id(1L).build();
		Property property = Property.builder().owner(userWithPermission).manager(userWithPermission).build();
		Booking booking = Booking.builder().property(property).user(User.builder().build()).build();
		assertDoesNotThrow(() -> validatorService.validateUserCanUpdateBooking(userWithPermission.getId(), booking));
	}

	@Test
	@DisplayName("Validates if user can update a booking - User of the booking")
	void validateUserCanUpdateBokingUserFromTheBookingTest() {
		User userWithPermission = User.builder().id(1L).build();
		Property property = Property.builder().owner(User.builder().id(2L).build())
				.manager(User.builder().id(3L).build()).build();
		Booking booking = Booking.builder().property(property).user(userWithPermission).build();
		assertDoesNotThrow(() -> validatorService.validateUserCanUpdateBooking(userWithPermission.getId(), booking));
	}

	@Test
	@DisplayName("Validates if user can update a booking - Invalid")
	void validateUserCanUpdateBokingtInvalidTest() {
		User userWithoutPermission = User.builder().id(1L).build();
		Property property = Property.builder().owner(User.builder().id(2L).build())
				.manager(User.builder().id(3L).build()).build();
		Booking booking = Booking.builder().property(property).user(User.builder().build()).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateUserCanUpdateBooking(userWithoutPermission.getId(), booking));
		assertEquals(BookingValidatorService.MSG_VALIDATE_USER_CAN_UPDATE_BOOKING, exception.getMessage());
	}

	@Test
	@DisplayName("Validates if the booking is blocked")
	void validateBookingIsBlockedTest() {
		Booking blockedBooking = Booking.builder().status(BookingStatus.BLOCKED).build();
		assertDoesNotThrow(() -> validatorService.validateBookingIsBlocked(blockedBooking));
	}

	@Test
	@DisplayName("Validates if the booking is blocked - Invalid")
	void validateBookingIsBlockedInvalidTest() {
		Booking activeBooking = Booking.builder().status(BookingStatus.ACTIVE).build();
		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateBookingIsBlocked(activeBooking));
		assertEquals(BookingValidatorService.MSG_VALIDATE_BOOKING_IS_BLOCKED, exception.getMessage());
	}

}
