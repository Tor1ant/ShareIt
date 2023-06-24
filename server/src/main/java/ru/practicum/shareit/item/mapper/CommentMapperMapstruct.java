package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;


@Mapper(componentModel = "spring")
public interface CommentMapperMapstruct {

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> commentToCommentDto(List<Comment> comments);
}
