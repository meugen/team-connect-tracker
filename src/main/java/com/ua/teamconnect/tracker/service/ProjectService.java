package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.ProjectMapper;
import com.ua.teamconnect.tracker.model.dto.ProjectDto;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.ProjectRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectDto> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userById(userId);
        }
        var projectIds = userProjectRepository.findProjectIdsByUserIdAndNow(userId, LocalDate.now());
        var projects = projectRepository.findAllById(projectIds);
        return projectMapper.entityIterableToDtoList(projects);
    }

    public List<ProjectDto> findByUserEmail(String email) {
        return userRepository.findByEmail(email)
            .map(user -> findByUserId(user.getId()))
            .orElseThrow(
                () -> NotFoundException.userByEmail(email)
            );
    }
}