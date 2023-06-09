package ru.practicum.shareit.booking.dto;


import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.BookingStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingInputDto {

    @FutureOrPresent(message = "бронирование не может состояться в прошлом.")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @NotNull
    private LocalDateTime start;
    @Future(message = "бронирование не может быть окончено в будущем.")
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    @NotNull
    private LocalDateTime end;
    @NotNull(message = "item не задан.")
    private Long itemId;
    private BookingStatus status;
}
