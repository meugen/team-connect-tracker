package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Integer> {
}
