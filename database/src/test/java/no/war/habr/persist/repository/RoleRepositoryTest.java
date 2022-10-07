package no.war.habr.persist.repository;

import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for RoleRepository")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-tc.properties")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @AfterAll
    public static void afterAll(@Autowired UserRepository userRepository,
                                @Autowired RoleRepository roleRepository) {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("findByName returns Role when successful")
    void shouldFindRoleByName() {
        // given
        // create role user
        Role roleUser = Role.builder().name(ERole.ROLE_USER).build();
        underTest.save(roleUser);

        // when
        Role role = underTest.findByName(ERole.ROLE_USER);

        // then
        assertThat(role).isEqualTo(roleUser);
    }

    @Test
    @DisplayName("findByName returns null when role does not exists")
    void shouldReturnNullWhenRoleDoesNotExists() {
        // given
        // when
        Role role = underTest.findByName(ERole.ROLE_ADMIN);

        // then
        assertThat(role).isNull();
    }
}