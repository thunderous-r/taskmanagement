package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "API для управления комментариями к задачам")
public class CommentController {
    private final TaskService taskService;

    @PostMapping("/{taskId}")
    @Operation(
            summary = "Добавление комментария к задаче",
            description = "Добавляет новый комментарий к указанной задаче. Администраторы, создатели задач и исполнители могут добавлять комментарии."
    )
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId, @RequestBody Comment comment, Authentication authentication) {
        return ResponseEntity.ok(taskService.addComment(taskId, comment, authentication.getName()));
    }

    @GetMapping("/{taskId}")
    @Operation(
            summary = "Получение комментариев к задаче",
            description = "Возвращает список комментариев к указанной задаче с поддержкой пагинации"
    )
    public ResponseEntity<Page<Comment>> getComments(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(taskService.getComments(taskId, pageable));
    }
}
