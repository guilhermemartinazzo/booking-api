package com.bookingapi.bookingapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookingapi.bookingapi.model.dto.requestbody.PropertyDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.PropertyResponseDTO;
import com.bookingapi.bookingapi.service.PropertyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/property")
@RequiredArgsConstructor
@Tag(name = "Property Services")
public class PropertyController {

	private final PropertyService service;

	@GetMapping
	public ResponseEntity<List<PropertyResponseDTO>> findAll() {
		List<PropertyResponseDTO> properties = service.findAll();
		return ResponseEntity.ok(properties);
	}

	@PostMapping
	public ResponseEntity<PropertyResponseDTO> create(@RequestBody PropertyDTO property) {
		PropertyResponseDTO propertyCreated = service.create(property);
		return new ResponseEntity<>(propertyCreated, HttpStatus.CREATED);
	}

}
