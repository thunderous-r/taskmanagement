package com.example.taskmanagement.controller;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final TaskService taskService;

    @PostMapping("/{taskId}")
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId, @RequestBody Comment comment, Authentication authentication) {
        return ResponseEntity.ok(taskService.addComment(taskId, comment, authentication.getName()));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Page<Comment>> getComments(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(taskService.getComments(taskId, pageable));
    }
}
