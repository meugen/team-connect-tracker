package com.ua.teamconnect.tracker.repository.specification.user.position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import static com.ua.teamconnect.tracker.util.StringUtil.escapeForLike;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor(access = PRIVATE)
public class SearchUserPositionSpecification implements Specification<UserPosition> {

    public static Specification<UserPosition> wrap(Specification<UserPosition> spec, String search) {
        if (isEmpty(search)) return spec;
        return spec.and(new SearchUserPositionSpecification(search));
    }

    private final String search;

    @Override
    public @Nullable Predicate toPredicate(
        @NonNull Root<UserPosition> root,
        @Nullable CriteriaQuery<?> query,
        @NonNull CriteriaBuilder criteriaBuilder
    ) {
        var parts = search.strip().toLowerCase().split("\\s+", 2);

        var firstName = criteriaBuilder.lower(root.get("user").get("firstName"));
        var lastName = criteriaBuilder.lower(root.get("user").get("lastName"));
        var firstNamePattern = "%" + escapeForLike(parts[0]) + "%";

        if (parts.length == 1) {
            return criteriaBuilder.or(
                criteriaBuilder.like(firstName, firstNamePattern, '\\'),
                criteriaBuilder.like(lastName, firstNamePattern, '\\')
            );
        }

        var lastNamePattern = escapeForLike(parts[1]) + "%";

        return criteriaBuilder.and(
            criteriaBuilder.like(firstName, firstNamePattern, '\\'),
            criteriaBuilder.like(lastName, lastNamePattern, '\\')
        );
    }
}
