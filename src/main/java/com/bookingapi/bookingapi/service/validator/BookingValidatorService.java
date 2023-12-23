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

	private final BookingRepository bookingRepository;

	public void validateUserInformedIsTypeGuest(User user) {
		if (!UserType.GUEST.equals(user.getUserType())) {
			throw new BusinessException("Only users of type GUEST can request a booking!");
		}
	}

	public void validateBookingPersistence(Long bookingId, LocalDate startDate, LocalDate endDate, Long propertyId)
			throws BusinessException {
		validateBlocksFromProperty(bookingId, startDate, endDate, propertyId);
		validateBookingDates(startDate, endDate);
	}

	public void validateBookingIsAlreadyCanceled(Booking booking) {
		if (BookingStatus.CANCELED.equals(booking.getStatus())) {
			throw new BusinessException("The Booking is already canceled");
		}
	}

	public void validateBookingMustBeCanceledToRebook(Booking booking) {
		if (!BookingStatus.CANCELED.equals(booking.getStatus())) {
			throw new BusinessException("The booking must be canceled perform this action");
		}
	}

	public void validateBookingStatusChangingToBlocked(BookingStatus bookingStatus, Booking bookingEntity) {
		boolean statusChanged = !bookingEntity.getStatus().equals(bookingStatus);
		if (BookingStatus.BLOCKED.equals(bookingStatus) && statusChanged) {
			// someone is trying to update a booking to status block
			throw new BusinessException("It's not possible to Block this booking");
		}
	}

	public void validateUserHasPermissionToBlock(User user, Property property) {
		if (!verifyUserHasPermissionToBlock(property, user)) {
			throw new BusinessException("This user is not able to create/update a block for this property",
					HttpStatus.FORBIDDEN);
		}
	}

	public void validatePropertyHasActiveOrBlockedBookings(BlockDTO blockDTO) {
		boolean hasActiveBookingsThatDates = !CollectionUtils.isEmpty(findBookingsFromPropertyBetweenDatesWithStatus(
				blockDTO, List.of(BookingStatus.ACTIVE, BookingStatus.BLOCKED)));
		if (hasActiveBookingsThatDates) {
			throw new BusinessException("The property has active bookings or blocks between these dates");
		}
	}

	public void validatePropertyHasCanceledBookings(BlockDTO blockDTO) {
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
			throw new BusinessException("User not able to change this booking", HttpStatus.FORBIDDEN);
		}
	}

	public void validateBookingIsBlocked(Booking booking) {
		if (!BookingStatus.BLOCKED.equals(booking.getStatus())) {
			throw new BusinessException("The booking must be with status Blocked to be deleted");
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

	private void validateBookingDates(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw new BusinessException("StartDate must be before EndDate");
		}
	}

	private void validateBlocksFromProperty(Long bookingId, LocalDate startDate, LocalDate endDate, Long propertyId)
			throws BusinessException {
		List<Booking> blocks = findBlocksFromPropertyBetweenDates(propertyId, startDate, endDate);
		if (!CollectionUtils.isEmpty(blocks) && blocks.stream().noneMatch(block -> block.getId().equals(bookingId))) {
			throw new BusinessException("Booking not available for this property in this date.");
		}
	}

	private List<Booking> findBlocksFromPropertyBetweenDates(Long idProperty, LocalDate startDate, LocalDate endDate) {
		return bookingRepository.findBlocksFromPropertyBetweenDates(idProperty, startDate, endDate);
	}

}
