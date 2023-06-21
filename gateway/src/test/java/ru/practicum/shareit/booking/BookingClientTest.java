package ru.practicum.shareit.booking;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient client;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void setApproved() throws JsonProcessingException {
        boolean approved = true;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        String response = objectMapper.writeValueAsString(booking);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        ResponseEntity<Object> objectResponseEntity = this.client.setApproved(1L, 1L, approved);
        Assertions.assertNotNull(objectResponseEntity);
        Assertions.assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
    }

    @Test
    void createBooking() throws JsonProcessingException {
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);

        String requestJson = objectMapper.writeValueAsString(
                bookingInputDto);

        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(requestJson, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = this.client.createBooking(bookingInputDto, 1L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getBooking() throws JsonProcessingException {
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);
        String response = objectMapper.writeValueAsString(bookingInputDto);
        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = this.client.getBooking(1L, 1L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getAllBookings() throws JsonProcessingException {
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);

        BookingInputDto bookingInputDto2 = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);

        String string = objectMapper.writeValueAsString(List.of(bookingInputDto, bookingInputDto2));

        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=3"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = this.client.getAllBookings(1L, "ALL", 0L, 3L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object bookings = responseEntity.getBody();
        Assertions.assertNotNull(bookings);
    }

    @Test
    void getAllOwnerBookings() throws JsonProcessingException {
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);

        BookingInputDto bookingInputDto2 = new BookingInputDto(LocalDateTime.now(), LocalDateTime.now().plusHours(10),
                1L, BookingStatus.WAITING);

        String string = objectMapper.writeValueAsString(List.of(bookingInputDto, bookingInputDto2));

        mockRestServiceServer.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL&from=0&size=3"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> responseEntity = this.client.getAllOwnerBookings(1L, "ALL", 0L, 3L);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object bookings = responseEntity.getBody();
        Assertions.assertNotNull(bookings);
    }
}