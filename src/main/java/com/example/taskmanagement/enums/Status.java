package com.example.taskmanagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус задачи")
public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
