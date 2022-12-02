package no.war.habr.persist.specification;

import no.war.habr.persist.model.EUserCondition;
import no.war.habr.persist.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;

public class UserSpecification {

    public static Specification<User> id(Long userId) {
        return (root, query, builder) ->
                builder.equal(root.get("id"), userId);
    }

    public static Specification<User> usernameLike(String username) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> username(String username) {
        return (root, query, builder) ->
                builder.equal(root.get("username"), username.strip());
    }

    public static Specification<User> firstNameLike(String firstName) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> lastNameLike(String lastName) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> condition(EUserCondition condition) {
        return (root, query, builder) ->
                builder.equal(root.get("condition"), condition);
    }

    public static Specification<User> fetchRoles() {
        return (root, query, builder) -> {
            query.distinct(true);
            root.fetch("roles", JoinType.LEFT);
            return builder.conjunction();
        };
    }

    public static Specification<User> fetchPosts() {
        return (root, query, builder) -> {
            query.distinct(true);
            root.fetch("posts", JoinType.LEFT);
            return builder.conjunction();
        };
    }
}
