package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentDtoPublic;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.errors.DataConflictRequest;
import ru.practicum.ewm.errors.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final EventService eventService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;


    // Часть Private

    // Добавление комментария к событию
    @Override
    public CommentDto addCommentToEvent(Long authorId, Long eventId, CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        Event event = eventService.getEventById(eventId);
        User author = userService.getUserById(authorId);
        if (event.getState().equals(EventState.PUBLISHED)) {  // published
            comment.setAuthor(author);
            comment.setEvent(event);
            comment.setCreate(LocalDateTime.now());
            log.info("Добавлен новый комментарий к событию ID = {} пользователем c ID = {}.", eventId, authorId);
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            log.info("Добавление комментария невозможно, событие ID = {} не опубликовано.", eventId);
            throw new DataConflictRequest("The event has not been published yet.");
        }
    }

    // Получение комментария по ID
    @Override
    public CommentDto getCommentByUser(Long authorId, Long commentId) {
        log.info("Получение комментария с ID = {} для просмотра пользователем с ID = {}.", commentId, authorId);
        return commentMapper.toCommentDto(getCommentById(commentId));
    }

    // Получение комментариев к событию
    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId) {
        Event event = eventService.getEventById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            List<Comment> comments = commentRepository.findAllByEventOrderByEvent(event);
            log.info("Получение комментариев к событию с ID = {}.", eventId);
            return comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        } else {
            log.info("Комментирование события с ID = {} пока не доступно, оно не опубликовано.", eventId);
            throw new DataConflictRequest("The event has not been published yet. Commenting is not available.");
        }
    }

    // Обновление комментария по ID автором
    @Override
    public CommentDto updateCommentByUser(Long authorId, Long commentId, CommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        User author = userService.getUserById(authorId);
        if (comment.getAuthor().getId() == (author.getId())) {
            comment.setText(commentDto.getText());
            comment.setCreate(LocalDateTime.now());
            log.info("Комментарий к событию ID = {} обновлен автором.", commentId);
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            log.info("Изменение комментария ID = {} невозможно, пользователь не является автором.", commentId);
            throw new DataConflictRequest("The user is not the author of the comment.");
        }
    }

    // Удавление комментария автором
    @Override
    public void deleteCommentByUser(Long authorId, Long commentId) {
        Comment comment = getCommentById(commentId);
        if (comment.getAuthor().getId() == authorId) {
            log.info("Комментарий с ID = {}, успешно удален автором комментария с ID = {}.", commentId, authorId);
            commentRepository.deleteById(getCommentById(commentId).getId());
        } else {
            throw new DataConflictRequest("The user is not the author of the comment.");
        }
    }


    // Часть Admin

    // Обновление комментария администратором
    @Override
    public CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        comment.setText(commentDto.getText());
        comment.setCreate(LocalDateTime.now());
        log.info("Комментарий к событию ID = {} откорректирован администратором.", commentId);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    // Удаление комментария администратором
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Комментарий с ID = {}, успешно удален администратором.", commentId);
        commentRepository.deleteById(getCommentById(commentId).getId());
    }


    // Часть Public

    // Получение всех комментариев к событию для публичного просмотра
    @Override
    public List<CommentDtoPublic> getAllCommentsByEventPublic(Long eventId) {
        Event event = eventService.getEventById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            List<Comment> comments = commentRepository.findAllByEventOrderByEvent(event);
            log.info("Получение комментариев к событию с ID = {}.", eventId);
            return comments.stream().map(commentMapper::toCommentDtoPublic).collect(Collectors.toList());
        } else {
            log.info("Комментирование события с ID = {} пока не доступно, оно не опубликовано.", eventId);
            throw new DataConflictRequest("The event has not been published yet. Commenting is not available.");
        }
    }


    // Вспомогательная часть
    public Comment getCommentById(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            return commentOptional.get();
        }
        throw new NotFoundException("Comment with ID = " + commentId + " was not found.");
    }
}
