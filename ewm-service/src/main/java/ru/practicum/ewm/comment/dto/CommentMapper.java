package ru.practicum.ewm.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "create", ignore = true)
    Comment toComment(CommentDto commentDto);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "event.id", target = "eventId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "event.id", target = "eventId")
    CommentDtoPublic toCommentDtoPublic(Comment comment);
}
