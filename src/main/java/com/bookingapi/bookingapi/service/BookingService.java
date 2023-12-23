package com.bookingapi.bookingapi.service;

import org.springframework.stereotype.Service;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.exception.BusinessException;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final BookingRepository repository;
	private final PropertyService propertyService;
	private final UserService userService;

	private final BookingValidatorService bookingValidator;

	public BookingResponseDTO createBooking(CreateBookingDTO bookingDTO) throws BusinessException {
		final User user = userService.findUserById(bookingDTO.userId());
		final Property property = propertyService.findById(bookingDTO.propertyId());
		bookingValidator.validateUserInformedIsTypeGuest(user);
		bookingValidator.validateBookingPersistence(null, bookingDTO.startDate(), bookingDTO.endDate(),
				property.getId());
		final Booking booking = buildBookingEntityFromDTO(bookingDTO, user, property);
		return buildBookingResponse(repository.save(booking));
	}

	public void delete(Long id, Long userId) {
		final Booking bookingToBeDeleted = findBookingById(id);
		bookingValidator.validateUserCanUpdateBooking(userId, bookingToBeDeleted);
		repository.delete(bookingToBeDeleted);
	}

	public BookingResponseDTO update(Long id, UpdateBookingDTO bookingDTO) {
		final Booking booking = findBookingById(id);
		final User user = userService.findUserById(bookingDTO.userId());
		final Property prop = booking.getProperty();
		bookingValidator.validateUserCanUpdateBooking(bookingDTO.userId(), booking);
		bookingValidator.validateBookingPersistence(id, bookingDTO.startDate(), bookingDTO.endDate(), prop.getId());
		return updateBooking(bookingDTO, booking, user);
	}

	public BookingResponseDTO createBlock(BlockDTO blockDTO) {
		final User user = userService.findUserById(blockDTO.userId());
		final Property property = propertyService.findById(blockDTO.propertyId());
		bookingValidator.validateBookingDates(blockDTO.startDate(), blockDTO.endDate());
		bookingValidator.validateUserHasPermissionToBlock(user, property);
		bookingValidator.validatePropertyHasActiveOrBlockedBookings(blockDTO);
		bookingValidator.validatePropertyHasCanceledBookings(blockDTO);
		Booking block = buildBookingFromBlockDTO(blockDTO, user, property);
		return buildBookingResponse(repository.save(block));

	}

	public BookingResponseDTO updateBlock(Long bookingId, BlockDTO blockDTO) {
		final Booking booking = findBookingById(bookingId);
		final Property property = booking.getProperty();
		final User user = userService.findUserById(blockDTO.userId());
		bookingValidator.validateUserHasPermissionToBlock(user, property);
		bookingValidator.validatePropertyHasActiveOrBlockedBookings(blockDTO);
		bookingValidator.validatePropertyHasCanceledBookings(blockDTO);
		booking.setDetails(blockDTO.details());
		booking.setStartDate(blockDTO.startDate());
		booking.setEndDate(blockDTO.endDate());
		return buildBookingResponse(repository.save(booking));
	}

	public void deleteBlock(Long bookingId, Long userId) {
		final Booking booking = findBookingById(bookingId);
		final User user = userService.findUserById(userId);
		bookingValidator.validateUserHasPermissionToBlock(user, booking.getProperty());
		bookingValidator.validateBookingIsBlocked(booking);
		repository.delete(booking);
	}

	public BookingResponseDTO findBookingResponseDTOById(Long id) {
		Booking booking = findBookingById(id);
		return buildBookingResponse(booking);
	}

	public Booking findBookingById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
	}

	public BookingResponseDTO cancelBooking(Long bookingId, Long userId) {
		Booking booking = findBookingById(bookingId);
		bookingValidator.validateBookingIsAlreadyCanceled(booking);
		bookingValidator.validateUserCanUpdateBooking(userId, booking);
		booking.setStatus(BookingStatus.CANCELED);
		repository.save(booking);
		return buildBookingResponse(booking);

	}

	public BookingResponseDTO rebookCanceledBooking(Long bookingId, RebookingCanceledDTO payload) {
		Booking booking = findBookingById(bookingId);
		bookingValidator.validateBookingMustBeCanceledToRebook(booking);
		bookingValidator.validateUserCanUpdateBooking(payload.userId(), booking);
		bookingValidator.validateBookingPersistence(bookingId, payload.startDate(), payload.endDate(),
				booking.getProperty().getId());
		return rebookCanceledBooking(payload, booking);

	}

	private Booking buildBookingEntityFromDTO(CreateBookingDTO bookingDTO, final User user, final Property property) {
		return Booking.builder().startDate(bookingDTO.startDate()).endDate(bookingDTO.endDate())
				.status(BookingStatus.ACTIVE).property(property).user(user).details(bookingDTO.details()).build();
	}

	private BookingResponseDTO updateBooking(UpdateBookingDTO bookingDTO, final Booking booking, final User user) {
		booking.setUser(user);
		booking.setStartDate(bookingDTO.startDate());
		booking.setEndDate(bookingDTO.endDate());
		booking.setDetails(bookingDTO.details());
		return buildBookingResponse(repository.save(booking));
	}

	private Booking buildBookingFromBlockDTO(BlockDTO blockDTO, User user, Property prop) {
		return Booking.builder().startDate(blockDTO.startDate()).endDate(blockDTO.endDate())
				.status(BookingStatus.BLOCKED).user(user).property(prop).details(blockDTO.details()).build();
	}

	private BookingResponseDTO rebookCanceledBooking(RebookingCanceledDTO payload, Booking booking) {
		booking.setStartDate(payload.startDate());
		booking.setEndDate(payload.endDate());
		booking.setStatus(BookingStatus.ACTIVE);
		booking.setDetails(payload.details());
		return buildBookingResponse(repository.save(booking));
	}

	private BookingResponseDTO buildBookingResponse(Booking booking) {
		return BookingResponseDTO.builder().id(booking.getId()).startDate(booking.getStartDate())
				.endDate(booking.getEndDate()).emailUser(booking.getUser().getEmail()).details(booking.getDetails())
				.propertyDescription(booking.getProperty().getDescription()).status(booking.getStatus()).build();
	}

}
