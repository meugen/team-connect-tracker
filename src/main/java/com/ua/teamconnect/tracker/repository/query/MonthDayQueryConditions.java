package com.ua.teamconnect.tracker.repository.query;

public final class MonthDayQueryConditions {

    private MonthDayQueryConditions() {
    }
    
    public static final String HIRE_DATE_IN_RANGE = """
       (:startMonth < extract(month from uh.hireDate) and extract(month from uh.hireDate) < :endMonth)
       or (:startMonth = extract(month from uh.hireDate) and extract(month from uh.hireDate) < :endMonth and :startDay <= extract(day from uh.hireDate))
       or (:startMonth < extract(month from uh.hireDate) and extract(month from uh.hireDate) = :endMonth and extract(day from uh.hireDate) <= :endDay)
       or (:startMonth = extract(month from uh.hireDate) and extract(month from uh.hireDate) = :endMonth and :startDay <= extract(day from uh.hireDate) and extract(day from uh.hireDate) <= :endDay)
       """;

    public static final String BIRTH_DATE_IN_RANGE = """
       (:startMonth < extract(month from u.birthDate) and extract(month from u.birthDate) < :endMonth)
       or (:startMonth = extract(month from u.birthDate) and extract(month from u.birthDate) < :endMonth and :startDay <= extract(day from u.birthDate))
       or (:startMonth < extract(month from u.birthDate) and extract(month from u.birthDate) = :endMonth and extract(day from u.birthDate) <= :endDay)
       or (:startMonth = extract(month from u.birthDate) and extract(month from u.birthDate) = :endMonth and :startDay <= extract(day from u.birthDate) and extract(day from u.birthDate) <= :endDay)
       """;
}
