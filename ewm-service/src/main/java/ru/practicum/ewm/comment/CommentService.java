package ru.practicum.ewm.comment;

import java.util.List;

public interface CommentService {

    CommentDto addCommentToEvent(Long authorId, Long eventId, CommentDto commentDto);

    CommentDto getCommentByUser(Long authorId, Long commentId);

    List<CommentDto> getAllCommentsByUser(Long authorId);

    CommentDto updateCommentByUser(Long authorId, Long commentId, Long commentId1);

    CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto);

    void deleteCommentByAdmin(Long commentId);

    CommentDto getComment(Long commentId);
}
