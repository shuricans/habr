package no.war.habr.persist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * Picture entity.
 * Represents the image.
 *
 * @author Karachev Sasha
 */
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "pictures_picture_id_seq",
            sequenceName = "pictures_picture_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "pictures_picture_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "picture_id")
    private Long id;

    @Column(name = "name", length = 1024, nullable = false)
    private String name;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "storage_file_name", length = 256, nullable = false, unique = true)
    private String storageFileName;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
