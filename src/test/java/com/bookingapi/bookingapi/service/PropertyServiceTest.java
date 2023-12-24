package com.bookingapi.bookingapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookingapi.bookingapi.model.dto.requestbody.PropertyDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.PropertyResponseDTO;
import com.bookingapi.bookingapi.model.entity.Property;
import com.bookingapi.bookingapi.model.entity.User;
import com.bookingapi.bookingapi.repository.PropertyRepository;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

	@InjectMocks
	private PropertyService service;

	@Mock
	private UserService userService;

	@Mock
	private PropertyRepository propertyRepository;

	@Test
	@DisplayName("Creating a property")
	void createPropertyTest() {
		User manager = User.builder().id(1L).build();
		User owner = User.builder().id(2L).build();
		PropertyDTO propertyDTO = PropertyDTO.builder().managerId(manager.getId()).ownerId(owner.getId())
				.description("description").build();
		Mockito.when(userService.findUserById(propertyDTO.managerId())).thenReturn(manager);
		Mockito.when(userService.findUserById(propertyDTO.ownerId())).thenReturn(owner);
		Property propertyExpected = Property.builder().description(propertyDTO.description()).manager(manager)
				.owner(owner).id(1L).build();
		Mockito.when(propertyRepository.save(any(Property.class))).thenReturn(propertyExpected);
		PropertyResponseDTO obtained = service.create(propertyDTO);
		assertThat(obtained).hasFieldOrPropertyWithValue("id", propertyExpected.getId())
				.hasFieldOrPropertyWithValue("description", propertyExpected.getDescription())
				.hasFieldOrPropertyWithValue("managerEmail", propertyExpected.getManager().getEmail())
				.hasFieldOrPropertyWithValue("ownerEmail", propertyExpected.getOwner().getEmail());
		verify(propertyRepository, times(1)).save(any(Property.class));
		verify(userService, times(2)).findUserById(anyLong());
	}

	@Test
	@DisplayName("Find all properties")
	void findAllTest() {
		List<Property> lstProperties = List
				.of(Property.builder().description("Description").manager(new User()).owner(new User()).id(1L).build());
		Mockito.when(propertyRepository.findAll()).thenReturn(lstProperties);
		List<PropertyResponseDTO> obtained = service.findAll();
		assertThat(obtained).hasSameSizeAs(lstProperties);
		verify(propertyRepository, times(1)).findAll();
	}
}
