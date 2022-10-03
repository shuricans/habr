package no.war.habr.util;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtils {

    public static <T> Specification<T> combineSpec(Specification<T> s1, Specification<T> s2) {
        return s1 == null ? Specification.where(s2) : s1.and(s2);
    }
}
