package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.CUSTOM_USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        item = new Item(1L, user, "testItem", "testDescription", true,
                null);
        user = new User();
        user.setName("testUser");
        user.setEmail("test@email.ru");
        user.setId(1L);
    }

    @DisplayName("Проверка создания бронирования")
    @Test
    void addWithValidInputShouldReturnBooking() throws Exception {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(10))
                .itemId(item.getId())
                .build();

        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingService.createBooking(Mockito.any(BookingInputDto.class), Mockito.anyLong()))
                .thenReturn(expectedBooking);

        mockMvc.perform(post("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));

        Mockito.verify(bookingService).createBooking(Mockito.any(BookingInputDto.class), Mockito.anyLong());
    }

    @DisplayName("Проверка создания бронирования приводящая к исключению")
    @Test
    void addWithInvalidInputShouldReturnBadRequest() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusHours(5))
                .itemId(item.getId())
                .build();
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(post("/bookings")
                                .header(CUSTOM_USER_ID_HEADER, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingInputDto)))
                        .andExpect(status().isBadRequest()));
    }

    @DisplayName("Проверка подтверждения бронирования")
    @Test
    void setApproveWithValidInputShouldReturnBooking() throws Exception {
        long ownerId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(bookingId);
        Mockito.when(bookingService.setApproved(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(expectedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(CUSTOM_USER_ID_HEADER, ownerId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Mockito.verify(bookingService).setApproved(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @DisplayName("Проверка подтверждения бронирования")
    @Test
    void getBookingWithValidInputShouldReturnBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        Booking expectedBooking = new Booking();
        expectedBooking.setId(bookingId);
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(expectedBooking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(CUSTOM_USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Mockito.verify(bookingService).getBooking(Mockito.anyLong(), Mockito.anyLong());
    }

    @DisplayName("Проверка получения всех бронирований")
    @Test
    void getAllUserBookingsWithValidInputShouldReturnListOfBookings() throws Exception {
        long userId = 1L;
        String state = "ALL";
        long from = 0L;
        long size = 10L;

        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        Mockito.when(bookingService.getAllBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());

        Mockito.verify(bookingService).getAllBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @DisplayName("Проверка получения всх бронирований владельцем")
    @Test
    void getAllOwnersBookingsWithValidInputShouldReturnListOfBookings() throws Exception {
        long userId = 1L;
        String state = "ALL";
        long from = 0L;
        long size = 10L;

        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        Mockito.when(bookingService.getAllOwnerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(expectedBookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(CUSTOM_USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());

        Mockito.verify(bookingService).getAllOwnerBookings(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyLong());
    }

    @DisplayName("Проверка невозможности установления отрицательного значения для параметра from")
    @Test
    void getAllUserBookings_WithInvalidFromValue_ShouldReturnBadRequest() {
        long userId = 1L;
        String state = "ALL";
        long from = -1L;
        long size = 10L;

        Assertions.assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/bookings")
                                .header(CUSTOM_USER_ID_HEADER, userId)
                                .param("state", state)
                                .param("from", String.valueOf(from))
                                .param("size", String.valueOf(size)))
                        .andExpect(status().isBadRequest()));

        Mockito.verify(bookingService, Mockito.never()).getAllBookings(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyLong(), Mockito.anyLong());
    }

    @DisplayName("Проверка невозможности установления отрицательного значения для параметра size")
    @Test
    void getAllUserBookings_WithInvalidSizeValue_ShouldReturnBadRequest() {
        long userId = 1L;
        String state = "ALL";
        long from = 0L;
        long size = -1L;

        Assertions.assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/bookings")
                        .header(CUSTOM_USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest()));

        Mockito.verify(bookingService, Mockito.never()).getAllBookings(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyLong(), Mockito.anyLong());
    }
}