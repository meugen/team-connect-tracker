package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.TaskMapper;
import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto create(TaskRequestDto requestDto) {
        var entity = new Task();
        taskMapper.requestDtoToEntity(requestDto, entity);
        entity = taskRepository.save(entity);
        return taskMapper.entityToDto(entity);
    }
}
