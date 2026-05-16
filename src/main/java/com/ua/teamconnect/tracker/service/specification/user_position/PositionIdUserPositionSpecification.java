package com.ua.teamconnect.tracker.service.specification.user_position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.Set;

import static com.ua.teamconnect.tracker.util.StringUtil.parseIntSet;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor(access = PRIVATE)
public class PositionIdUserPositionSpecification implements Specification<UserPosition> {

    public static Specification<UserPosition> wrap(
        Specification<UserPosition> spec, String position
    ) {
        if (isEmpty(position)) return spec;
        var set = parseIntSet(position);
        return spec.and(new PositionIdUserPositionSpecification(set));
    }

    private final Set<Integer> positionIds;

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<UserPosition> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var cq = query == null ? criteriaBuilder.createQuery() : query;
        var userPosition = cq.from(UserPosition.class);
        var userIdSubquery = cq.subquery(UserPosition.class)
            .select(userPosition.get("id").get("userId"))
            .where(userPosition.get("id").get("positionId").in(positionIds));
        return criteriaBuilder.in(root.get("id").get("userId")).value(userIdSubquery);
    }
}
