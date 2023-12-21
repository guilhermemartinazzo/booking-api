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
import org.springframework.web.bind.annotation.RestController;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.BookingDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.CancelBookingDTO;
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

	/**
	 * Get a booking by id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Find a booking by your ID", method = "GET")
	public ResponseEntity<BookingResponseDTO> findBookingById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findBookingResponseDTOById(id));
	}

	/**
	 * Creates a booking
	 * 
	 * @param booking
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	@Operation(summary = "Creates a booking, informing endDate, startDate, property and user,", method = "POST")
	public ResponseEntity<BookingResponseDTO> create(@Validated(CreateGroup.class) @RequestBody BookingDTO payload)
			throws Exception {
		BookingResponseDTO bookingCreated = service.createBooking(payload);
		return new ResponseEntity<>(bookingCreated, HttpStatus.CREATED);
	}

	/**
	 * Update a booking
	 * 
	 * @param id
	 * @param booking
	 * @return
	 */
	@PutMapping("/{id}")
	@Operation(summary = "Updates an existing booking, you can change the booking dates, user, property, status ", method = "PUT")
	public ResponseEntity<BookingResponseDTO> update(@Validated(UpdateGroup.class) @PathVariable Long id,
			@RequestBody BookingDTO payload) {
		BookingResponseDTO updatedBooking = service.update(id, payload);
		return ResponseEntity.ok(updatedBooking);
	}

	/**
	 * Delete a booking
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "Deletes a booking from the system", method = "DELETE")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 
	 */
	@PutMapping("/cancel")
	@Operation(summary = "Cancel a booking if it's active", method = "PUT")
	public ResponseEntity<BookingResponseDTO> cancelBooking(@RequestBody CancelBookingDTO payload) {
		return ResponseEntity.ok(service.cancelBooking(payload));
	}
    
	@PostMapping("/block")
	@Operation(summary = "Creates a block for a property", method = "POST")
	public ResponseEntity<BookingResponseDTO> createBlock(@RequestBody BlockDTO payload) {
		return ResponseEntity.ok(service.createBlock(payload));
	}
	/**
	@PutMapping("/block/{id}")
	@Operation(summary = "Update a block for a property", method = "PUT")
	public ResponseEntity<Void> updateBlock(@PathVariable Long id) {
		service.updateBlock(id);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/block/{id}")
	@Operation(summary = "Creates a block for a property", method = "DELETE")
	public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
		service.deleteBlock(id);
		return ResponseEntity.noContent().build();
	}
	*/
}
