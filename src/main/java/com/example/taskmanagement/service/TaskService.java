package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.enums.Role;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    @Transactional
    public Task createTask(Task task, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setCreator(creator);
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Page<Task> getTasks(String creator, String assignee, Pageable pageable) {
        if (creator != null) {
            return taskRepository.findByCreator_Email(creator, pageable);
        } else if (assignee != null) {
            return taskRepository.findByAssignee_Email(assignee, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, String email) {
        Task task = getTaskById(id);
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN &&
        !task.getCreator().getEmail().equals(email) &&
                (task.getAssignee() == null || !task.getAssignee().getEmail().equals(email))) {
                    throw new AccessDeniedException("You don't have permission to update this task");
                }

        if (user.getRole() != Role.ADMIN &&
        !task.getCreator().equals(email) &&
        task.getAssignee() != null &&
        task.getAssignee().getEmail().equals(email)) {
            task.setStatus(updatedTask.getStatus());
            return taskRepository.save(task);
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());

        if (user.getRole() == Role.ADMIN || task.getCreator().getEmail().equals(email)) {
            task.setAssignee(updatedTask.getAssignee());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id, String email) {
        Task task = getTaskById(id);
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN && !task.getCreator().getEmail().equals(email)) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }

        taskRepository.deleteById(id);
    }

    @Transactional
    public Comment addComment(Long taskId, Comment comment, String creatorEmail) {
        Task task = getTaskById(taskId);
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (creator.getRole() != Role.ADMIN && !task.getCreator().getEmail().equals(creatorEmail) &&
                (task.getAssignee() == null || !task.getAssignee().getEmail().equals(creatorEmail))) {
            throw new AccessDeniedException("You don't have permission to add a comment to this task");
        }

        comment.setTask(task);
        comment.setCreator(creator);
        return commentRepository.save(comment);
    }

    public Page<Comment> getComments(Long taskId, Pageable pageable) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found");
        }
        return commentRepository.findByTask_Id(taskId, pageable);
    }
}
