package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.entity.id.UserProjectId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserProjectRepository extends CrudRepository<UserProject, UserProjectId> {

    @Query("""
    select up from UserProject up join fetch up.project where up.id.userId=:userId
        and up.id.projectId in (select p.id from Project p where p.startDate <= :now
            and (p.endDate is null or :now < p.endDate))
        and up.startDate <= :now and (up.endDate is null or :now < up.endDate)
    """)
    List<UserProject> findByUserIdAndNow(Long userId, LocalDate now);
}
