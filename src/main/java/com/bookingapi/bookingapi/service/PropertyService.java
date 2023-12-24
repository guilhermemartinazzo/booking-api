package com.bookingapi.bookingapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.model.dto.requestbody.PropertyDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.PropertyResponseDTO;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.PropertyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PropertyService {

	private final PropertyRepository repository;
	private final UserService userService;

	public PropertyResponseDTO create(PropertyDTO propertyDTO) {
		User manager = userService.findUserById(propertyDTO.managerId());
		User owner = userService.findUserById(propertyDTO.ownerId());
		Property prop = new Property();
		prop.setDescription(propertyDTO.description());
		prop.setManager(manager);
		prop.setOwner(owner);
		return buildPropertyResponseDTO(repository.save(prop));
	}

	public List<PropertyResponseDTO> findAll() {
		List<Property> properties = repository.findAll();
		return properties.stream().map(this::buildPropertyResponseDTO).toList();
	}

	private PropertyResponseDTO buildPropertyResponseDTO(Property prop) {
		return PropertyResponseDTO.builder().description(prop.getDescription()).id(prop.getId())
				.managerEmail(prop.getManager().getEmail()).ownerEmail(prop.getOwner().getEmail())
				.activeBookings(getBookingsFromPropertyWithStatus(prop, BookingStatus.ACTIVE)).build();
	}

	private long getBookingsFromPropertyWithStatus(Property property, BookingStatus status) {
		return CollectionUtils.isEmpty(property.getBookings()) ? 0
				: property.getBookings().stream().filter(prop -> status.equals(prop.getStatus())).count();
	}

	public Property findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Property not found"));
	}
}
