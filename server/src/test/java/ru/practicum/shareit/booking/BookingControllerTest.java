package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequestDto requestDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        UserDto booker = new UserDto();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@test.com");

        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);

        responseDto = new BookingResponseDto();
        responseDto.setId(10L);
        responseDto.setStart(requestDto.getStart());
        responseDto.setEnd(requestDto.getEnd());
        responseDto.setItem(item);
        responseDto.setBooker(booker);
        responseDto.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class), eq(2L))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void createBooking_withInvalidDates_shouldReturnBadRequest() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class), eq(2L)))
                .thenThrow(new ValidationException("Start date must be before end date"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveOrRejectBooking_shouldReturnUpdatedBooking() throws Exception {
        responseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveOrRejectBooking(eq(10L), eq(1L), eq(true))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 10L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveOrRejectBooking_byNonOwner_shouldReturnBadRequest() throws Exception {
        when(bookingService.approveOrRejectBooking(eq(10L), eq(3L), eq(true)))
                .thenThrow(new ValidationException("Only the owner of this item can approve the booking"));

        mockMvc.perform(patch("/bookings/{bookingId}", 10L)
                        .header("X-Sharer-User-Id", 3L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        when(bookingService.get(10L, 2L)).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 10L)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void getBookingsByBooker_shouldReturnList() throws Exception {
        List<BookingResponseDto> list = List.of(responseDto);
        when(bookingService.getAllBookingsByBooker(eq(2L), eq(BookingState.ALL))).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void getBookingsByBooker_withStateParam_shouldPassState() throws Exception {
        List<BookingResponseDto> list = List.of(responseDto);
        when(bookingService.getAllBookingsByBooker(eq(2L), eq(BookingState.FUTURE))).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOwnerBookings_shouldReturnList() throws Exception {
        List<BookingResponseDto> list = List.of(responseDto);
        when(bookingService.getAllBookingsByOwner(eq(1L), eq(BookingState.ALL))).thenReturn(list);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}