package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.enums.Priority;
import com.example.taskmanagement.enums.Role;
import com.example.taskmanagement.enums.Status;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private TaskService taskService;

    private User adminUser;
    private User regularUser;
    private User assigneeUser;
    private Task task;

    @BeforeEach
    void setUp() {
        adminUser = new User(1L, "admin@example.com", "password", Role.ADMIN);
        regularUser = new User(2L, "user@example.com", "password", Role.USER);
        assigneeUser = new User(3L, "assignee@example.com", "password", Role.USER);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.PENDING);
        task.setCreator(regularUser);
        task.setAssignee(assigneeUser);
    }

    @Test
    void createTask_ValidUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(regularUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task newTask = new Task();
        newTask.setTitle("New Task");

        Task result = taskService.createTask(newTask, "user@example.com");
        assertNotNull(result);
        assertEquals(regularUser, result.getCreator());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTaskById_ExistingTask_ReturnsTask() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(1L);

        assertNotNull(foundTask);
        assertEquals(1l, foundTask.getId());
    }

    @Test
    void updateTask_AsAdmin_SuccessfulUpdate() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(adminUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTaskData = new Task();
        updatedTaskData.setTitle("Updated Title");
        updatedTaskData.setDescription("Updated Description");
        updatedTaskData.setStatus(Status.IN_PROGRESS);

        Task result = taskService.updateTask(1L, updatedTaskData, "admin@example.com");

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_AsAssignee_OnlyUpdateStatus() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(assigneeUser.getEmail())).thenReturn(Optional.of(assigneeUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTaskData = new Task();
        updatedTaskData.setTitle("Updated Title");
        updatedTaskData.setStatus(Status.IN_PROGRESS);

        Task result = taskService.updateTask(1L, updatedTaskData, assigneeUser.getEmail());

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertNotEquals("Updated Title", result.getTitle());
    }

    @Test
    void deleteTask_UnauthorizedUser_ThrowsException() {
        User anotherUser = new User(4L, "another@example.com", "password", Role.USER);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anotherUser.getEmail())).thenReturn(Optional.of(anotherUser));

        assertThrows(AccessDeniedException.class, () ->
                taskService.deleteTask(1L, anotherUser.getEmail())
        );

        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void getTasks_WithCreatorFilter_ReturnsFilteredTasks() {
        Page<Task> page = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findByCreator_Email(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.getTasks("user@example.com", null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

}
