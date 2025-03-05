package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public List<Comment> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
