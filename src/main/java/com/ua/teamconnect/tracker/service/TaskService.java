package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.TaskMapper;
import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.model.exception.DuplicateException;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto create(TaskRequestDto requestDto) {
        if (taskRepository.existsByName(requestDto.name())) {
            throw DuplicateException.task();
        }
        var entity = new Task();
        taskMapper.requestDtoToEntity(requestDto, entity);
        entity = taskRepository.save(entity);
        return taskMapper.entityToDto(entity);
    }

    public TaskDto update(Integer id, TaskRequestDto requestDto) {
        if (taskRepository.existsByNameAndIdNot(requestDto.name(), id)) {
            throw DuplicateException.task();
        }
        var entity = taskRepository.findById(id).orElseThrow(
            () -> NotFoundException.task(id)
        );
        taskMapper.requestDtoToEntity(requestDto, entity);
        entity = taskRepository.save(entity);
        return taskMapper.entityToDto(entity);
    }
}
