package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "API для управления задачами")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(
            summary = "Создание новой задачи",
            description = "Создает новую задачу с указанными параметрами. Текущий пользователь становится создателем задачи."
    )
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task, Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(task, authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение задачи по ID",
            description = "Возвращает подробную информацию о задаче по её идентификатору"
    )
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    @Operation(
            summary = "Получение списка задач",
            description = "Возвращает список задач с возможностью фильтрации по создателю или исполнителю, а также пагинацией"
    )
    public ResponseEntity<Page<Task>> getTasks(@RequestParam(required = false) String creator,
                                               @RequestParam(required = false) String assignee,
                                               Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(creator, assignee, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновление задачи",
            description = "Обновляет существующую задачу. Администраторы могут обновлять любые задачи, " +
                    "обычные пользователи - только свои или те, где они назначены исполнителями"
    )
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody Task task,
                                           Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTask(id, task, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление задачи",
            description = "Удаляет существующую задачу. Администраторы или создатели задачи могут удалять задачи"
    )
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
