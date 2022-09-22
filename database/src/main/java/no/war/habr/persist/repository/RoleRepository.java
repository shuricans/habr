package no.war.habr.persist.repository;

import no.war.habr.persist.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
