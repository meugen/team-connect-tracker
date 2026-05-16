package com.ua.teamconnect.tracker.service.specification.user_position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

import static com.ua.teamconnect.tracker.util.StringUtil.parseIntSet;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor(access = PRIVATE)
public class DepartmentIdUserPositionSpecification implements Specification<UserPosition> {

    public static Specification<UserPosition> wrap(
        Specification<UserPosition> spec, String department
    ) {
        if (isEmpty(department)) return spec;
        var set = parseIntSet(department);
        return spec.and(new DepartmentIdUserPositionSpecification(set));
    }

    private final Set<Integer> departmentIds;

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
            .where(userPosition.get("position").get("departmentId").in(departmentIds));
        return criteriaBuilder.in(root.get("id").get("userId")).value(userIdSubquery);
    }
}
