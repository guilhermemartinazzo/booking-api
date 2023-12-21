package com.bookingapi.bookingapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.enumerator.UserType;
import com.bookingapi.bookingapi.exception.BusinessException;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.BookingDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.CancelBookingDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.model.entity.Booking;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.BookingRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final BookingRepository repository;
	private final PropertyService propertyService;
	private final UserService userService;

	public BookingResponseDTO createBooking(BookingDTO bookingDTO) throws BusinessException {
		final User user = userService.findUserById(bookingDTO.userId());
		final Property property = propertyService.findById(bookingDTO.propertyId());
		validateUserInformedIsTypeGuest(user);
		validateBookingPersistence(bookingDTO, property, user);
		final Booking booking = Booking.builder().startDate(bookingDTO.startDate()).endDate(bookingDTO.endDate())
				.status(BookingStatus.ACTIVE).property(property).user(user).build();
		repository.save(booking);
		return buildBookingResponse(booking);

	}

	public void delete(Long id) {
		final Booking bookingToBeDeleted = findBookingById(id);
		repository.delete(bookingToBeDeleted);
	}

	public BookingResponseDTO update(Long id, BookingDTO bookingDTO) {
		final Booking booking = findBookingById(id);
		final User user = userService.findUserById(bookingDTO.userId());
		final Property prop = propertyService.findById(bookingDTO.propertyId());
		validateUserCanUpdateBooking(bookingDTO.userId(), booking);
		verifyBookingStatusChangingToBlocked(bookingDTO, booking);
		validateBookingPersistence(bookingDTO, prop, user);
		return updateBooking(bookingDTO, booking, user, prop);
	}

	public BookingResponseDTO createBlock(BlockDTO blockDTO) {
		final User user = userService.findUserById(blockDTO.userId());
		final Property property = propertyService.findById(blockDTO.propertyId());
		validateUserHasPermissionToBlock(user, property);
		validatePropertyHasActiveOrBlockedBookings(blockDTO);
		validatePropertyHasCanceledBookings(blockDTO);
		Booking block = buildBookingFromBlockDTO(blockDTO, user, property);
		repository.save(block);
		return buildBookingResponse(block);

	}

	public BookingResponseDTO findBookingResponseDTOById(Long id) {
		Booking booking = findBookingById(id);
		return buildBookingResponse(booking);
	}

	public Booking findBookingById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
	}

	public BookingResponseDTO cancelBooking(CancelBookingDTO payload) {
		Booking booking = findBookingById(payload.bookingId());

		verifyBookingIsAlreadyCanceled(booking);
		validateUserCanUpdateBooking(payload.userId(), booking);
		booking.setStatus(BookingStatus.CANCELED);
		repository.save(booking);
		return buildBookingResponse(booking);

	}

	public BookingResponseDTO rebookCanceledBooking(Long id, BookingDTO payload) {
		Booking booking = findBookingById(id);
		if (BookingStatus.CANCELED.equals(booking.getStatus())) {
			validateUserCanUpdateBooking(payload.userId(), booking);
			validateBookingPersistence(payload, booking.getProperty(), booking.getUser());
			return rebookCanceledBooking(payload, booking);
		} else {
			throw new BusinessException("The booking must be canceled to rebook");
		}

	}

	private BookingResponseDTO updateBooking(BookingDTO bookingDTO, final Booking booking, final User user,
			final Property prop) {
		booking.setUser(user);
		booking.setProperty(prop);
		booking.setStartDate(bookingDTO.startDate());
		booking.setEndDate(bookingDTO.endDate());
		booking.setStatus(bookingDTO.status());
		return buildBookingResponse(repository.save(booking));
	}

	private void verifyBookingStatusChangingToBlocked(BookingDTO bookingDTO, Booking bookingEntity) {
		boolean statusChanged = !bookingEntity.getStatus().equals(bookingDTO.status());
		if (BookingStatus.BLOCKED.equals(bookingDTO.status()) && statusChanged) {
			// someone is trying to update a booking to status block
			throw new BusinessException("It's not possible to Block this booking");
		}
	}

	private Booking buildBookingFromBlockDTO(BlockDTO blockDTO, User user, Property prop) {
		return Booking.builder().startDate(blockDTO.startDate()).endDate(blockDTO.endDate())
				.status(BookingStatus.BLOCKED).user(user).property(prop).build();
	}

	private void validatePropertyHasCanceledBookings(BlockDTO blockDTO) {
		List<Booking> canceledBookings = findBookingsFromPropertyBetweenDatesWithStatus(blockDTO,
				List.of(BookingStatus.CANCELED));
		boolean hasCanceledBookingsThoseDates = !CollectionUtils.isEmpty(canceledBookings);
		if (hasCanceledBookingsThoseDates) {
			// deletes canceled bookings
			repository.deleteAll(canceledBookings);
		}
	}

	private void validatePropertyHasActiveOrBlockedBookings(BlockDTO blockDTO) {
		boolean hasActiveBookingsThatDates = !CollectionUtils.isEmpty(findBookingsFromPropertyBetweenDatesWithStatus(
				blockDTO, List.of(BookingStatus.ACTIVE, BookingStatus.BLOCKED)));
		if (hasActiveBookingsThatDates) {
			throw new BusinessException("The property has active bookings or blocks between these dates");
		}
	}

	private void validateUserHasPermissionToBlock(User user, Property property) {
		if (!verifyUserHasPermissionToBlock(property, user)) {
			throw new BusinessException("This user is not able to create a block for this property");
		}
	}

	private BookingResponseDTO rebookCanceledBooking(BookingDTO payload, Booking booking) {
		booking.setStartDate(payload.startDate());
		booking.setEndDate(payload.endDate());
		booking.setStatus(BookingStatus.ACTIVE);
		return buildBookingResponse(repository.save(booking));
	}

	private void validateUserCanUpdateBooking(Long userId, Booking booking) {
		boolean isUserGuestOfBooking = userId.equals(booking.getUser().getId());
		boolean isUserManagerOfProperty = userId.equals(booking.getProperty().getManager().getId());
		boolean isUserOwnerOfProperty = userId.equals(booking.getProperty().getOwner().getId());
		List<Boolean> userConditionsToCancel = List.of(isUserGuestOfBooking, isUserManagerOfProperty,
				isUserOwnerOfProperty);
		if (!userConditionsToCancel.contains(Boolean.TRUE)) {
			// Only the guest or property manager/owner can update a booking
			throw new BusinessException("User not able to change this booking");
		}
	}

	private void verifyBookingIsAlreadyCanceled(Booking booking) {
		if (BookingStatus.CANCELED.equals(booking.getStatus())) {
			throw new BusinessException("The Booking is already canceled");
		}
	}

	private BookingResponseDTO buildBookingResponse(Booking booking) {
		return BookingResponseDTO.builder().id(booking.getId()).startDate(booking.getStartDate())
				.endDate(booking.getEndDate()).emailGuest(booking.getUser().getEmail())
				.propertyDescription(booking.getProperty().getDescription()).status(booking.getStatus()).build();
	}

	/**
	 * 
	 * private List<BookingResponseDTO> findBookingsFromProperty(Long idProperty,
	 * BookingStatus status) { List<Booking> bookingsFromProperty =
	 * repository.findByPropertyIdAndStatus(idProperty, status);
	 * List<BookingResponseDTO> bookingsDTO = new ArrayList<>();
	 * bookingsFromProperty.stream().forEach(booking ->
	 * bookingsDTO.add(buildBookingResponse(booking))); return bookingsDTO; }
	 * 
	 */
	private List<Booking> findBlocksFromPropertyBetweenDates(Long idProperty, LocalDate startDate, LocalDate endDate) {
		return repository.findBlocksFromPropertyBetweenDates(idProperty, startDate, endDate);
	}

	private void validateBookingPersistence(BookingDTO dto, Property property, User user) throws BusinessException {
		findBlocksFromProperty(dto, property);
		validateBookingDates(dto);
	}

	private void validateUserInformedIsTypeGuest(User user) {
		if (!UserType.GUEST.equals(user.getUserType())) {
			throw new BusinessException("Only users of type GUEST can request a booking!");
		}
	}

	private void validateBookingDates(BookingDTO dto) {
		if (dto.startDate().isAfter(dto.endDate())) {
			throw new BusinessException("StartDate must be before EndDate");
		}
	}

	private void findBlocksFromProperty(BookingDTO dto, Property propertyEntity) throws BusinessException {
		List<Booking> blocks = findBlocksFromPropertyBetweenDates(propertyEntity.getId(), dto.startDate(),
				dto.endDate());
		if (!CollectionUtils.isEmpty(blocks) && blocks.stream().noneMatch(block -> block.getId().equals(dto.id()))) {
			throw new BusinessException("Booking not available for this property in this date.");
		}
	}

	private boolean verifyUserHasPermissionToBlock(Property prop, User user) {
		// User of type guest is not allowed to block a booking
		List<Long> managerAndOwnerIdsOfProperty = List.of(prop.getManager().getId(), prop.getOwner().getId());
		return managerAndOwnerIdsOfProperty.contains(user.getId());
	}

	private List<Booking> findBookingsFromPropertyBetweenDatesWithStatus(BlockDTO block, List<BookingStatus> status) {
		return repository.findBookingsFromPropertyBetweenDates(block.propertyId(), block.startDate(), block.endDate(),
				status);
	}

}
