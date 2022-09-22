package no.war.habr.persist.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static no.war.habr.persist.model.EUserCondition.ACTIVE;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "users_user_id_seq",
            sequenceName = "users_user_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "users_user_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, length = 20)
    private EUserCondition condition = ACTIVE;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "owner"
    )
    private List<Post> posts = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
