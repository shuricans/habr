package no.war.habr.persist.repository;

import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(ERole name);
}
