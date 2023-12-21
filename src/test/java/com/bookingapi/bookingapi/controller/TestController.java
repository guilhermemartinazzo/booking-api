package com.bookingapi.bookingapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@WebAppConfiguration
public class TestController {

	@Autowired
	private WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	ObjectMapper objMapper;

	@BeforeEach
	private void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		objMapper = new ObjectMapper();
		objMapper.registerModule(new JavaTimeModule());
	}

}
