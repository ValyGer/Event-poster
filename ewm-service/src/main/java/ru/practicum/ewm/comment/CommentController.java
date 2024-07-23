package ru.practicum.ewm.comment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final EventService eventService;
    private final CommentService commentService;

    // Часть private

    // Добавление комментариев к событию
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addCommentToEvent(@NonNull @PathVariable("userId") Long authorId,
                                                        @NonNull @PathVariable("eventId") Long eventId,
                                                        @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызов метода добавление комментария (addCommentToEvent)");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addCommentToEvent(authorId, eventId, commentDto));
    }

    // Получение конкретного комментария пользователя
    @GetMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentByUser(@NonNull @PathVariable("userId") Long authorId,
                                                       @NonNull @PathVariable("commentId") Long commentId) {
        log.info("Вызов метода получение комментария с ID = {} пользователем с ID = {} (getCommentByUser)", authorId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentByUser(authorId, commentId));
    }

    // Получение всех комментариев пользователя
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getAllCommentsByUser(@NonNull @PathVariable("userId") Long authorId) {
        log.info("Вызов метода получение всех комментариев пользователя с ID = {} (getAllCommentsByUser)", authorId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsByUser(authorId));
    }

    // Редактирование комментария к событию
    @PatchMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentByUser(@NonNull @PathVariable("userId") Long authorId,
                                                          @NonNull @PathVariable("commentId") Long commentId,
                                                          @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызов метода обновления комментария с ID = {} пользователем с ID = {} (updateCommentByUser)",
                authorId, commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.updateCommentByUser(authorId, commentId, commentId));
    }


    // Часть admin

    // Изменение конкретного комментария
    @PatchMapping("admin/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentByAdmin(@NonNull @PathVariable("commentId") Long commentId,
                                                           @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызов метода обновления комментария с ID = {} администратором (updateCommentByAdmin)",
                commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateCommentByAdmin(commentId, commentDto));
    }

    // Удаление комментариев
    @DeleteMapping("admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@NonNull @PathVariable("commentId") Long commentId) {
        log.info("Вызов метода удаления комментария с ID = {} администратором (deleteCommentByAdmin)",
                commentId);
        commentService.deleteCommentByAdmin(commentId);
    }


    // Часть public

    // Получение конкретного комментария к событию
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> getComment(@NonNull @PathVariable("commentId") Long commentId) {
        log.info("Вызов метода получения комментария с ID = {} неавторизованным пользователем (getComment)", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComment(commentId));
    }
}
