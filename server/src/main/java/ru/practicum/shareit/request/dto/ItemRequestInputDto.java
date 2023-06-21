package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemRequestInputDto {

    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
