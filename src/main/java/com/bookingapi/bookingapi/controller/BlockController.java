package com.bookingapi.bookingapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookingapi.bookingapi.group.CreateGroup;
import com.bookingapi.bookingapi.group.DeleteGroup;
import com.bookingapi.bookingapi.group.UpdateGroup;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/booking/block")
@RequiredArgsConstructor
@Tag(name = "Block Services")
public class BlockController {

	private final BookingService service;

	@PostMapping
	@Operation(summary = "Creates a block for a property", method = "POST")
	public ResponseEntity<BookingResponseDTO> createBlock(@Validated(CreateGroup.class) @RequestBody BlockDTO payload) {
		return ResponseEntity.ok(service.createBlock(payload));
	}

	@PutMapping("/{bookingId}")
	@Operation(summary = "Updates a block by it's id", method = "PUT")
	public ResponseEntity<BookingResponseDTO> updateBlock(@PathVariable(name = "bookingId") Long bookingId,
			@Validated(UpdateGroup.class) @RequestBody BlockDTO payload) {
		return ResponseEntity.ok(service.updateBlock(bookingId, payload));
	}

	@DeleteMapping("/{bookingId}")
	@Operation(summary = "Deletes a block by it's id", method = "DELETE")
	public ResponseEntity<Void> deleteBlock(@PathVariable(name = "bookingId") Long bookingId,
			@Validated(DeleteGroup.class) @RequestParam(name = "userId") Long userId) {
		service.deleteBlock(bookingId, userId);
		return ResponseEntity.noContent().build();
	}
}
