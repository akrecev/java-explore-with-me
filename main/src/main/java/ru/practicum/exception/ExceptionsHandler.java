package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.utility.Formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final BadRequestException badRequestException) {
        return badRequest(badRequestException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final MethodArgumentNotValidException validException) {
        return badRequest(validException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final MethodArgumentTypeMismatchException mismatchException) {
        return badRequest(mismatchException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final MissingServletRequestParameterException missingParameterException) {
        return badRequest(missingParameterException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException conflictException) {
        return conflict(conflictException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final DataIntegrityViolationException violationException) {
        return conflict(violationException);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final DataNotFoundException notFoundException) {
        log.warn("404 {}", notFoundException.getMessage());
        notFoundException.printStackTrace(printWriter);

        return ApiError.builder()
                .errors(Collections.singletonList(stringWriter.toString()))
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(notFoundException.getMessage())
                .timestamp(LocalDateTime.now().format(Formatter.TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final Throwable throwable) {
        log.error("500 {}", throwable.getMessage(), throwable);
        throwable.printStackTrace(printWriter);

        return ApiError.builder()
                .errors(Collections.singletonList(stringWriter.toString()))
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Integrity constraint has been violated.")
                .message(throwable.getMessage())
                .timestamp(LocalDateTime.now().format(Formatter.TIME_FORMATTER))
                .build();
    }

    private ApiError badRequest(final Exception e) {
        log.error("400 {}", e.getMessage());
        e.printStackTrace(printWriter);

        return ApiError.builder()
                .errors(Collections.singletonList(stringWriter.toString()))
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Formatter.TIME_FORMATTER))
                .build();
    }

    private ApiError conflict(final Exception e) {
        log.error("409 {}", e.getMessage());
        e.printStackTrace(printWriter);

        return ApiError.builder()
                .errors(Collections.singletonList(stringWriter.toString()))
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(Formatter.TIME_FORMATTER))
                .build();
    }

}
