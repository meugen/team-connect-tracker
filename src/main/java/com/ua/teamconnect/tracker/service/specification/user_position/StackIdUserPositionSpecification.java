package com.ua.teamconnect.tracker.service.specification.user_position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.entity.UserStack;
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
public class StackIdUserPositionSpecification implements Specification<UserPosition> {

    public static Specification<UserPosition> wrap(
        Specification<UserPosition> spec,
        String stack
    ) {
        if (isEmpty(stack)) return spec;
        var set = parseIntSet(stack);
        return spec.and(new StackIdUserPositionSpecification(set));
    }

    private final Set<Integer> stackIds;

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<UserPosition> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var cq = query == null ? criteriaBuilder.createQuery() : query;
        var userStack = cq.from(UserStack.class);
        var userIdSubquery = cq.subquery(UserStack.class)
            .select(userStack.get("id").get("userId"))
            .where(userStack.get("id").get("stackId").in(stackIds));
        return criteriaBuilder.in(root.get("id").get("userId")).value(userIdSubquery);
    }
}
