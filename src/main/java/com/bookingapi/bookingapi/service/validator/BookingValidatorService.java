package com.bookingapi.bookingapi.service.validator;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.exception.BusinessException;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.entity.Booking;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingValidatorService {

	static final String MSG_VALIDATE_BOOKING_IS_BLOCKED = "The booking must be with status Blocked to be deleted";
	static final String MSG_VALIDATE_USER_CAN_UPDATE_BOOKING = "User not able to modify this booking";
	static final String MSG_VALIDATE_PROPERTY_HAS_ACTIVE_OR_BLOCKED_BOOKINGS = "The property has active bookings or blocks between these dates";
	static final String MSG_VALIDATE_USER_HAS_PERMISSION_TO_BLOCK = "This user is not able to modify a block for this property";
	static final String MSG_VALIDATE_BOOKING_STATUS_CHANGING_TO_BLOCKED = "It's not possible to Block this booking";
	static final String MSG_VALIDATE_BOOKING_MUST_BE_CANCELED_TO_REBOOK = "The booking must be canceled to perform this action";
	static final String MSG_VALIDATE_BOOKING_IS_ALREADY_CANCELED = "The Booking is already canceled";
	static final String MSG_VALIDATE_BLOCKS_FROM_PROPERTY = "Booking not available for this property in this date";
	static final String MSG_VALIDATE_USER_INFORMED_IS_TYPE_GUEST = "Only users of type GUEST can request a booking";
	static final String MSG_VALIDATE_BOOKING_DATES = "StartDate must be before EndDate";

	private final BookingRepository bookingRepository;

	public void validateUserInformedIsTypeGuest(User user) {
		if (!UserType.GUEST.equals(user.getUserType())) {
			throw new BusinessException(MSG_VALIDATE_USER_INFORMED_IS_TYPE_GUEST);
		}
	}

	public void validateBookingPersistence(Long bookingId, LocalDate startDate, LocalDate endDate, Long propertyId)
			throws BusinessException {
		validateBlocksFromProperty(bookingId, startDate, endDate, propertyId);
		validateBookingDates(startDate, endDate);
	}

	public void validateBookingIsAlreadyCanceled(Booking booking) {
		if (BookingStatus.CANCELED.equals(booking.getStatus())) {
			throw new BusinessException(MSG_VALIDATE_BOOKING_IS_ALREADY_CANCELED);
		}
	}

	public void validateBookingMustBeCanceledToRebook(Booking booking) {
		if (!BookingStatus.CANCELED.equals(booking.getStatus())) {
			throw new BusinessException(MSG_VALIDATE_BOOKING_MUST_BE_CANCELED_TO_REBOOK);
		}
	}

	public void validateBookingStatusChangingToBlocked(BookingStatus bookingStatus, Booking bookingEntity) {
		boolean statusChanged = !bookingEntity.getStatus().equals(bookingStatus);
		if (BookingStatus.BLOCKED.equals(bookingStatus) && statusChanged) {
			// someone is trying to update a booking to status block
			throw new BusinessException(MSG_VALIDATE_BOOKING_STATUS_CHANGING_TO_BLOCKED);
		}
	}

	public void validateUserHasPermissionToBlock(User user, Property property) {
		if (!verifyUserHasPermissionToBlock(property, user)) {
			throw new BusinessException(MSG_VALIDATE_USER_HAS_PERMISSION_TO_BLOCK, HttpStatus.FORBIDDEN);
		}
	}

	public void validatePropertyHasActiveOrBlockedBookings(BlockDTO blockDTO) {
		boolean hasActiveBookingsThatDates = !CollectionUtils.isEmpty(findBookingsFromPropertyBetweenDatesWithStatus(
				blockDTO, List.of(BookingStatus.ACTIVE, BookingStatus.BLOCKED)));
		if (hasActiveBookingsThatDates) {
			throw new BusinessException(MSG_VALIDATE_PROPERTY_HAS_ACTIVE_OR_BLOCKED_BOOKINGS);
		}
	}

	public void verifyPropertyHasCanceledBookings(BlockDTO blockDTO) {
		List<Booking> canceledBookings = findBookingsFromPropertyBetweenDatesWithStatus(blockDTO,
				List.of(BookingStatus.CANCELED));
		boolean hasCanceledBookingsThoseDates = !CollectionUtils.isEmpty(canceledBookings);
		if (hasCanceledBookingsThoseDates) {
			// deletes canceled bookings
			bookingRepository.deleteAll(canceledBookings);
		}
	}

	public void validateUserCanUpdateBooking(Long userId, Booking booking) {
		boolean isUserGuestOfBooking = userId.equals(booking.getUser().getId());
		boolean isUserManagerOfProperty = userId.equals(booking.getProperty().getManager().getId());
		boolean isUserOwnerOfProperty = userId.equals(booking.getProperty().getOwner().getId());
		List<Boolean> userConditionsToAllow = List.of(isUserGuestOfBooking, isUserManagerOfProperty,
				isUserOwnerOfProperty);
		if (!userConditionsToAllow.contains(Boolean.TRUE)) {
			// Only the guest or property manager/owner can update a booking
			throw new BusinessException(MSG_VALIDATE_USER_CAN_UPDATE_BOOKING, HttpStatus.FORBIDDEN);
		}
	}

	public void validateBookingIsBlocked(Booking booking) {
		if (!BookingStatus.BLOCKED.equals(booking.getStatus())) {
			throw new BusinessException(MSG_VALIDATE_BOOKING_IS_BLOCKED);
		}
	}

	private List<Booking> findBookingsFromPropertyBetweenDatesWithStatus(BlockDTO block, List<BookingStatus> status) {
		return bookingRepository.findBookingsFromPropertyBetweenDates(block.propertyId(), block.startDate(),
				block.endDate(), status);
	}

	private boolean verifyUserHasPermissionToBlock(Property prop, User user) {
		// User of type guest is not allowed to block a booking
		List<Long> managerAndOwnerIdsOfProperty = List.of(prop.getManager().getId(), prop.getOwner().getId());
		return managerAndOwnerIdsOfProperty.contains(user.getId());
	}

	public void validateBookingDates(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException(MSG_VALIDATE_BOOKING_DATES);
		}
	}

	private void validateBlocksFromProperty(Long bookingId, LocalDate startDate, LocalDate endDate, Long propertyId)
			throws BusinessException {
		List<Booking> blocks = findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate);
		if (!CollectionUtils.isEmpty(blocks) && blocks.stream().noneMatch(block -> block.getId().equals(bookingId))) {
			throw new BusinessException(MSG_VALIDATE_BLOCKS_FROM_PROPERTY);
		}
	}

	private List<Booking> findBlocksFromPropertyBetweenDates(Long idProperty, LocalDate startDate, LocalDate endDate) {
		return bookingRepository.findBlocksFromPropertyBetweenDates(idProperty, startDate, endDate);
	}

}
