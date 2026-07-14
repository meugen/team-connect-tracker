package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.UserProjectTask;
import com.ua.teamconnect.tracker.model.entity.id.UserProjectTaskId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProjectTaskRepository extends CrudRepository<UserProjectTask, UserProjectTaskId> {
}
