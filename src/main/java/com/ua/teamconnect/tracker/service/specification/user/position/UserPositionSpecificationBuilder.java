package com.ua.teamconnect.tracker.service.specification.user.position;

import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.exception.UnsupportedFilterException;
import com.ua.teamconnect.tracker.service.specification.common.FetchSpecification;
import com.ua.teamconnect.tracker.util.Pair;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Component
public class UserPositionSpecificationBuilder {

    static final String PARAM_SEARCH = "search";
    static final String PARAM_DEPARTMENT = "department";
    static final String PARAM_POSITION = "position";
    static final String PARAM_STACK = "stack";
    private static final Set<String> SUPPORTED_PARAMS = Set.of(
                    PARAM_SEARCH,
                    PARAM_DEPARTMENT,
                    PARAM_POSITION,
                    PARAM_STACK,
                    "page",
                    "size",
                    "sort",
                    "order"
                );

    /**
     * first - specification for data
     * second - specification for count (without fetch)
     */
    public Pair<Specification<UserPosition>, Specification<UserPosition>> build(
        Map<String, String> params, Set<String> fetchFields
    ) {
        validateSupportedParams(params);
        var specification = Specification.<UserPosition>unrestricted();
        specification = SearchUserPositionSpecification.wrap(specification, params.get(PARAM_SEARCH));
        specification = DepartmentIdUserPositionSpecification.wrap(specification, params.get(PARAM_DEPARTMENT));
        specification = PositionIdUserPositionSpecification.wrap(specification, params.get(PARAM_POSITION));
        specification = StackIdUserPositionSpecification.wrap(specification, params.get(PARAM_STACK));
        var now = LocalDate.now();
        specification = CurrentUserPositionSpecification.wrap(specification, now);
        specification = SingleUserPositionSpecification.wrap(specification);
        var dataSpecification = FetchSpecification.wrap(specification, fetchFields);
        return new Pair<>(dataSpecification, specification);
    }

    public Pair<Specification<UserPosition>, Specification<UserPosition>> build(Map<String, String> params) {
        return build(params, Set.of("user", "position.department"));
    }
    
    private void validateSupportedParams(Map<String, String> params) {
        params.keySet().forEach(param -> {
            if (!SUPPORTED_PARAMS.contains(param)) {
                throw new UnsupportedFilterException(param);
            }
        });
    }
}
