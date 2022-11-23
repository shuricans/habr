package no.war.habr.persist.specification;

import no.war.habr.persist.model.EPostCondition;
import no.war.habr.persist.model.Post;
import no.war.habr.persist.model.Tag;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;


public class PostSpecification {

    public static Specification<Post> id(long postId) {
        return (root, query, builder) ->
                builder.equal(root.get("id"), postId);
    }

    public static Specification<Post> topic(String topic) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("topic").get("name")), topic.toLowerCase());
    }

    public static Specification<Post> hasTags(List<String> tags) {
        return (root, query, builder) -> {
            query.distinct(true);
            Join<Post, Tag> joinPostTag = root.join("tags", JoinType.LEFT);
            return joinPostTag.get("name").in(tags);
        };
    }

    public static Specification<Post> condition(EPostCondition condition) {
        return (root, query, builder) ->
                builder.equal(root.get("condition"), condition);
    }

    public static Specification<Post> fetchTags() {
        return (root, query, builder) -> {
            query.distinct(true);
            root.fetch("tags", JoinType.LEFT);
            return builder.conjunction();
        };
    }

    public static Specification<Post> fetchPictures() {
        return (root, query, builder) -> {
            query.distinct(true);
            root.fetch("pictures", JoinType.LEFT);
            return builder.conjunction();
        };
    }
}
