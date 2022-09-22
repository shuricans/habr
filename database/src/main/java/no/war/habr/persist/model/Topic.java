package no.war.habr.persist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "topics")
public class Topic extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "topics_topic_id_seq",
            sequenceName = "topics_topic_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "topics_topic_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "topic_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "topic"
    )
    private List<Post> posts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(name, topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
