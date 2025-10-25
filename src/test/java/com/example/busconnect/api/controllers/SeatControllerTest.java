package com.example.busconnect.api.controllers;

import com.example.busconnect.api.dto.SeatDtos.*;
import com.example.busconnect.domine.entities.enums.SeatType;
import com.example.busconnect.security.config.JwtService;
import com.example.busconnect.services.service.SeatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private SeatService seatService;
    @MockitoBean private JwtService jwtService;

    private SeatResponse seatResponse;

    @BeforeEach
    void setUp() {
        seatResponse = new SeatResponse(1L, "A12", "STANDARD", 1L, "ABC123", 40);
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void createSeat_ShouldCreateAndReturnSeat() throws Exception {
        SeatCreateRequest createRequest = new SeatCreateRequest("A12", SeatType.STANDARD, 1L);
        when(seatService.createSeat(any(SeatCreateRequest.class))).thenReturn(seatResponse);

        mockMvc.perform(post("/api/v1/seats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("A12"));

        verify(seatService).createSeat(any(SeatCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void getSeatsByBus_ShouldReturnBusSeats() throws Exception {
        when(seatService.getSeatsByBusId(1L)).thenReturn(Arrays.asList(seatResponse));

        mockMvc.perform(get("/api/v1/seats/bus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].busId").value(1));

        verify(seatService).getSeatsByBusId(1L);
    }
}