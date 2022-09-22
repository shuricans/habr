package no.war.habr.persist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @SequenceGenerator(
            name = "roles_role_id_seq",
            sequenceName = "roles_role_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "roles_role_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private ERole name;

    @ToString.Exclude
    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY
    )
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}



