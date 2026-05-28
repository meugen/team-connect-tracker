package com.ua.teamconnect.tracker.repository.specification.user.position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SingleUserPositionSpecification implements Specification<UserPosition> {

    private static final String USER_ID = "userId";

    public static Specification<UserPosition> wrap(Specification<UserPosition> spec) {
        return spec.and(new SingleUserPositionSpecification());
    }

    /**
     * In very edge case when user have two positions
     * and startDate in both are equal we'll still have multiple positions
     */
    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<UserPosition> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var cq = query == null ? criteriaBuilder.createQuery() : query;

        var userId = root.get("id").get(USER_ID);
        var maxStartDateSubquery = cq.subquery(LocalDate.class);
        var maxStartDateRoot = maxStartDateSubquery.from(UserPosition.class);
        maxStartDateSubquery.select(criteriaBuilder.<LocalDate>greatest(maxStartDateRoot.get("startDate")))
            .where(criteriaBuilder.equal(maxStartDateRoot.get("id").get(USER_ID), userId));
        // select max(up.startDate) from UserPosition up where up.id.userId=root.id.userId

        var positionIdSubquery = cq.subquery(Integer.class);
        var positionIdRoot = positionIdSubquery.from(UserPosition.class);
        positionIdSubquery.select(positionIdRoot.get("id").get("positionId"))
            .where(criteriaBuilder.and(
                criteriaBuilder.equal(positionIdRoot.get("id").get(USER_ID), userId),
                criteriaBuilder.equal(positionIdRoot.get("startDate"), maxStartDateSubquery)
            ));
        // select up1.position_id from UserPosition up1 where up1.id.userId=root.id.userId and
        // up1.startDate=(select max(up2.startDate) from UserPosition up2 where up2.id.userId=root.id.userId)

        return criteriaBuilder.equal(root.get("id").get("positionId"), positionIdSubquery);
    }
}
