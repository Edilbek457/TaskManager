package org.example.taskFlow.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse (int code, String message, LocalDateTime time)  {}
