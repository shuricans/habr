package no.war.habr.persist.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated")
    private LocalDateTime updated;
}
