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
@Table(name = "tags")
public class Tag extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "tags_tag_id_seq",
            sequenceName = "tags_tag_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "tags_tag_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 20)
    private String name;

    @ToString.Exclude
    @ManyToMany(
            mappedBy = "tags",
            fetch = FetchType.LAZY
    )
    private List<Post> posts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
