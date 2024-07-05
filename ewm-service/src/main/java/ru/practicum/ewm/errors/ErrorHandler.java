package ru.practicum.ewm.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundException(final NotFoundException e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(),
                List.of(stackTrace), LocalDateTime.now().format(formatter));

//    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse errorValidation(final ValidationException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse errorEmailAlreadyExists(final ConflictException e) {
//        return new ErrorResponse(
//                e.getMessage()
//        );
//    }
    }
}
