package ru.practicum.shareit.item.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputCommentDto {

    @NotNull(message = "комментарий не может быть пустым.")
    @NotBlank(message = "комментарий не может быть пустым.")
    private String text;
}
