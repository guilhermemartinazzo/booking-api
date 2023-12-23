package com.bookingapi.bookingapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.model.dto.requestbody.CreateBookingDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.RebookingCanceledDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.UpdateBookingDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/booking")
@RequiredArgsConstructor
@Tag(name = "Booking Services")
public class BookingController {

	private final BookingService service;

	@GetMapping("/{id}")
	@Operation(summary = "Finds a booking by ID", method = "GET")
	public ResponseEntity<BookingResponseDTO> findBookingById(@PathVariable(name = "id") Long id) {
		return ResponseEntity.ok(service.findBookingResponseDTOById(id));
	}

	@PostMapping
	@Operation(summary = "Creates a booking", method = "POST")
	public ResponseEntity<BookingResponseDTO> create(
			@Validated(CreateGroup.class) @RequestBody CreateBookingDTO payload) {
		BookingResponseDTO bookingCreated = service.createBooking(payload);
		return new ResponseEntity<>(bookingCreated, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Updates an existing booking", method = "PUT")
	public ResponseEntity<BookingResponseDTO> update(@PathVariable(name = "id") Long id,
			@Validated(UpdateGroup.class) @RequestBody UpdateBookingDTO payload) {
		BookingResponseDTO updatedBooking = service.update(id, payload);
		return ResponseEntity.ok(updatedBooking);
	}

	@PutMapping("/rebook/{bookingId}")
	@Operation(summary = "Rebooks an existing canceled booking", method = "PUT")
	public ResponseEntity<BookingResponseDTO> rebookCanceledBooking(@PathVariable(name = "bookingId") Long bookingId,
			@Validated @RequestBody RebookingCanceledDTO payload) {
		BookingResponseDTO updatedBooking = service.rebookCanceledBooking(bookingId, payload);
		return ResponseEntity.ok(updatedBooking);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Deletes a booking from the system", method = "DELETE")
	public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id, @RequestParam(name = "userId") Long userId) {
		service.delete(id, userId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/cancel")
	@Operation(summary = "Cancels a booking if it's active", method = "PUT")
	public ResponseEntity<BookingResponseDTO> cancelBooking(@RequestParam("id") Long bookingId,
			@RequestParam("userId") Long userId) {
		return ResponseEntity.ok(service.cancelBooking(bookingId, userId));
	}

}
