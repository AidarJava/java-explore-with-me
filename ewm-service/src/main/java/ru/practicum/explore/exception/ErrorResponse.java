package ru.practicum.explore.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    String status;
    String reason;
    String message;
    String timestamp;
}
