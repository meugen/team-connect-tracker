package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.TimeLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeLogRepository extends CrudRepository<TimeLog, Integer> {

    @Query("""
    select tl from TimeLog tl join fetch tl.userProjectTask upt
        join fetch upt.project join fetch upt.task
        where tl.userProjectTask.id.userId=:userId
        and :startDate <= tl.date and tl.date <= :endDate
    """)
    List<TimeLog> findAllInPeriod(Integer userId, LocalDate startDate, LocalDate endDate);
}
