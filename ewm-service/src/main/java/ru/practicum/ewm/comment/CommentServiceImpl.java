package ru.practicum.ewm.comment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Override
    public CommentDto addCommentToEvent(Long authorId, Long eventId, CommentDto commentDto) {
        return null;
    }

    @Override
    public CommentDto getCommentByUser(Long authorId, Long commentId) {
        return null;
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long authorId) {
        return null;
    }

    @Override
    public CommentDto updateCommentByUser(Long authorId, Long commentId, Long commentId1) {
        return null;
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto) {
        return null;
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {

    }

    @Override
    public CommentDto getComment(Long commentId) {
        return null;
    }
}
