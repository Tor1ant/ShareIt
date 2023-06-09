package ru.practicum.shareit.booking;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder rest) {
        super(rest
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> setApproved(Long ownerId, Long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", ownerId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> createBooking(BookingInputDto bookingInputDto, Long bookerId) {
        return post("", bookerId, bookingInputDto);
    }

    public ResponseEntity<Object> getBooking(Long userID, Long bookingId) {
        return get("/" + bookingId, userID);
    }

    public ResponseEntity<Object> getAllBookings(Long userID, String state, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userID, parameters);
    }

    public ResponseEntity<Object> getAllOwnerBookings(Long userID, String state, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userID, parameters);
    }
}
