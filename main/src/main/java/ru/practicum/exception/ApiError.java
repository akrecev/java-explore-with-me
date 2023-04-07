package ru.practicum.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private List<String> errors; // Список стектрейсов или описания ошибок

    private String message; // Сообщение об ошибке

    private String reason; // Общее описание причины ошибки

    private HttpStatus status; // Код статуса HTTP-ответа

    private String timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")


}
