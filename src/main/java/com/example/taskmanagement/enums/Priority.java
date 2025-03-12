package com.example.taskmanagement.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Приоритет задачи")
public enum Priority {
    HIGH,
    MEDIUM,
    LOW
}
