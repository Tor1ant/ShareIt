package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemRequestInputDto {
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
