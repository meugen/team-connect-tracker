package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.entity.id.UserPositionId;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPositionRepository extends CrudRepository<UserPosition, UserPositionId> {

    @Query("""
    select up from UserPosition up join fetch up.position p join fetch p.department
        where up.id.userId=:userId and up.startDate <= :now and (up.endDate is null or :now < up.endDate)
    """)
    List<UserPosition> findByUserIdAndNow(Long userId, LocalDateTime now);

    @Query("select up from UserPosition up where up.id.userId=:userId")
    List<UserPosition> findByUserId(Long userId, Limit limit);

    default Optional<LocalDateTime> findHireDateByUserId(Long userId) {
        return findByUserId(userId, Limit.of(1)).stream()
            .map(UserPosition::getStartDate)
            .findFirst();
    }
}
