package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.UserStack;
import com.ua.teamconnect.tracker.model.entity.id.UserStackId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStackRepository extends CrudRepository<UserStack, UserStackId> {

    @Query("select us from UserStack us join fetch us.stack where us.id.userId = :userId")
    List<UserStack> findByUserId(Integer userId);
}
