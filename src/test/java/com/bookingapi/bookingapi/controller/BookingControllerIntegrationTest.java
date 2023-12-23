package com.bookingapi.bookingapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.bookingapi.bookingapi.enumerator.BookingStatus;
import com.bookingapi.bookingapi.model.dto.requestbody.BlockDTO;
import com.bookingapi.bookingapi.model.dto.requestbody.CreateBookingDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.BookingResponseDTO;
import com.bookingapi.bookingapi.model.dto.responsebody.ExceptionResponseDTO;
import com.bookingapi.bookingapi.service.BookingService;

import jakarta.persistence.EntityNotFoundException;

@TestMethodOrder(OrderAnnotation.class)
class BookingControllerIntegrationTest extends TestController {

	@Autowired
	private BookingService bookingService;

	/**
	 * On initialization (resources > data.sql) we have this scenario: 3 users (id:
	 * 1, userType: Guest) | (id: 2 userType: Manager) | (id: 3, userType: Owner) 3
	 * properties (ids: 1,2,3) with the same managers and owners 1 booking on
	 * property 1 (2099-12-01 to 2099-12-05) yyyy-MM-dd
	 */
	private static final long ID_USER_GUEST = 1L;
	private static final long ID_USER_MANAGER = 2L;
	private static final long ID_USER_OWNER = 3L;
	private static final long ID_PROPERTY_1 = 1L;
	private static final long ID_PROPERTY_2 = 2L;
	private static final long ID_PROPERTY_3 = 3L;
	private static final long ID_BOOKING_1 = 1L;
	//

	@Order(10)
	@Test
	@DisplayName("Property Manager trying to create a block when there is an Active Booking on those dates")
	void testCreateBlockWhenThereIsActiveBooking_STATUS_BAD_REQUEST() throws Exception {
		// Preparing
		BlockDTO blockPayload = BlockDTO.builder().propertyId(ID_PROPERTY_1).startDate(LocalDate.of(2099, 12, 4))
				.endDate(LocalDate.of(2099, 12, 6)).userId(ID_USER_MANAGER).build();
		String jsonPayload = objMapper.writeValueAsString(blockPayload);
		// Executing and Validating
		mockMvc.perform(post("/v1/booking/block").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isBadRequest()).andReturn();

	}

	@Order(20)
	@Test
	@DisplayName("Property Manager creates a block when there is a Canceled Booking on those dates")
	void testCreateBlockWhenThereIsCanceledBooking_Status_OK() throws Exception {

		// Preparing
		bookingService.cancelBooking(ID_USER_GUEST, ID_BOOKING_1);
		BlockDTO blockPayload = BlockDTO.builder().propertyId(ID_PROPERTY_1).startDate(LocalDate.of(2099, 12, 1))
				.endDate(LocalDate.of(2099, 12, 4)).userId(ID_USER_MANAGER).build();
		String jsonPayload = objMapper.writeValueAsString(blockPayload);
		// Executing and Validating
		MvcResult result = mockMvc
				.perform(post("/v1/booking/block").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isOk()).andReturn();
		BookingResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				BookingResponseDTO.class);
		assertThrows(EntityNotFoundException.class, () -> {
			bookingService.findBookingById(ID_BOOKING_1);
		});
		assertNotNull(bookingService.findBookingById(responseObtained.id()));
	}

	@Order(30)
	@Test
	@DisplayName("User type GUEST trying to create a block")
	void testCreateBlockWhenWithUserTypeGuest_Status_BAD_REQUEST() throws Exception {

		// Preparing
		BlockDTO blockPayload = BlockDTO.builder().propertyId(ID_PROPERTY_1).startDate(LocalDate.of(2099, 12, 1))
				.endDate(LocalDate.of(2099, 12, 4)).userId(ID_USER_GUEST).build();
		String jsonPayload = objMapper.writeValueAsString(blockPayload);
		// Executing and Validating
		mockMvc.perform(post("/v1/booking/block").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isForbidden()).andReturn();

	}

	@Order(40)
	@Test
	@DisplayName("User type OWNER creates a block")
	void testCreateBlockWhenWithUserTypeOwner_Status_OK() throws Exception {

		// Preparing
		BlockDTO blockPayload = BlockDTO.builder().propertyId(ID_PROPERTY_1).startDate(LocalDate.of(2099, 12, 6))
				.endDate(LocalDate.of(2099, 12, 7)).userId(ID_USER_OWNER).build();
		String jsonPayload = objMapper.writeValueAsString(blockPayload);
		// Executing and Validating
		MvcResult result = mockMvc
				.perform(post("/v1/booking/block").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isOk()).andReturn();
		BookingResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				BookingResponseDTO.class);
		assertEquals(BookingStatus.BLOCKED, responseObtained.status());
		assertEquals(blockPayload.startDate(), responseObtained.startDate());
		assertEquals(blockPayload.endDate(), responseObtained.endDate());
	}

	@Order(50)
	@Test
	@DisplayName("User of type guest creates a booking on property 2")
	void testUserGuestCreateBooking_Status_OK() throws Exception {

		// Preparing
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().propertyId(ID_PROPERTY_2).startDate(LocalDate.of(2099, 12, 8))
				.endDate(LocalDate.of(2099, 12, 9)).userId(ID_USER_GUEST).build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		MvcResult result = mockMvc
				.perform(post("/v1/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isCreated()).andReturn();
		BookingResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				BookingResponseDTO.class);
		assertEquals(BookingStatus.ACTIVE, responseObtained.status());
		assertEquals(bookingPayload.startDate(), responseObtained.startDate());
		assertEquals(bookingPayload.endDate(), responseObtained.endDate());
	}

	@Order(60)
	@Test
	@DisplayName("User of type MANAGER trying to create a booking on property 2")
	void testUserManagerCreateBooking_Status_BAD_REQUEST() throws Exception {

		// Preparing
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().propertyId(ID_PROPERTY_2).startDate(LocalDate.of(2099, 12, 8))
				.endDate(LocalDate.of(2099, 12, 9)).userId(ID_USER_MANAGER).build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		mockMvc.perform(post("/v1/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isBadRequest()).andReturn();
	}

	@Order(70)
	@Test
	@DisplayName("User of type OWNER trying to create a booking on property 2")
	void testUserOwnerCreateBooking_Status_BAD_REQUEST() throws Exception {

		// Preparing
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().propertyId(ID_PROPERTY_2).startDate(LocalDate.of(2099, 12, 8))
				.endDate(LocalDate.of(2099, 12, 9)).userId(ID_USER_OWNER).build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		mockMvc.perform(post("/v1/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isBadRequest()).andReturn();
	}

	@Order(80)
	@Test
	@DisplayName("User GUEST trying to create a booking with past dates on property 2")
	void testUserGuestCreateBooking_Status_BAD_REQUEST() throws Exception {

		// Preparing
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().propertyId(ID_PROPERTY_2)
				.startDate(LocalDate.now().minusDays(1)).endDate(LocalDate.now()).userId(ID_USER_GUEST).build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		mockMvc.perform(post("/v1/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isBadRequest()).andReturn();
	}

	@Order(90)
	@Test
	@DisplayName("User GUEST trying to create a booking not informing mandatory fields")
	void testUserGuestCreateBookingNoRequiredFields_Status_BAD_REQUEST() throws Exception {

		// Preparing
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		MvcResult result = mockMvc
				.perform(post("/v1/booking").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isBadRequest()).andReturn();

		ExceptionResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				ExceptionResponseDTO.class);
		String message = responseObtained.getMessage();
		assertThat(message).contains("startDate").contains("endDate").contains("propertyId").contains("userId");
	}

	@Order(100)
	@Test
	@DisplayName("Guest creates a booking and then update dates on property 2")
	void testUserGuestUpdatesBooking_Status_OK() throws Exception {

		// Preparing
		BookingResponseDTO bookingCreatedProperty2 = bookingService
				.createBooking(CreateBookingDTO.builder().startDate(LocalDate.of(2098, 10, 01))
						.endDate(LocalDate.of(2098, 10, 02)).propertyId(ID_PROPERTY_2).userId(ID_USER_GUEST).build());
		CreateBookingDTO bookingPayload = CreateBookingDTO.builder().startDate(LocalDate.of(2098, 12, 01))
				.endDate(LocalDate.of(2098, 12, 02)).propertyId(ID_PROPERTY_2)
				.userId(ID_USER_GUEST).build();
		String jsonPayload = objMapper.writeValueAsString(bookingPayload);
		// Executing and Validating
		MvcResult result = mockMvc
				.perform(put("/v1/booking/{id}", bookingCreatedProperty2.id())
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonPayload))
				.andExpect(status().isOk()).andReturn();

		BookingResponseDTO responseObtained = objMapper.readValue(result.getResponse().getContentAsString(),
				BookingResponseDTO.class);
		assertThat(responseObtained).hasFieldOrPropertyWithValue("startDate", bookingPayload.startDate())
				.hasFieldOrPropertyWithValue("endDate", bookingPayload.endDate())
				.hasFieldOrPropertyWithValue("status", BookingStatus.ACTIVE)
				.hasFieldOrPropertyWithValue("id", bookingCreatedProperty2.id());
		bookingService.delete(bookingCreatedProperty2.id(), ID_USER_GUEST);
	}

}
