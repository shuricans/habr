package no.war.habr.persist.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Id
    @SequenceGenerator(
            name = "refresh_tokens_rt_id_seq",
            sequenceName = "refresh_tokens_rt_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "refresh_tokens_rt_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "rt_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
