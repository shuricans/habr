package no.war.habr.persist.repository;

import no.war.habr.persist.model.EUserCondition;
import no.war.habr.persist.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findByFilter(@Param("username") String username, @Param("birthday") LocalDate birthday, @Param("condition") EUserCondition condition);

    boolean existsByUsername(String username);
}
