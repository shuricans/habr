package no.war.habr.persist.repository;


import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import no.war.habr.persist.model.User;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for UserRepository")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-tc.properties")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;
    @Autowired
    private RoleRepository roleRepository;


    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        roleRepository.deleteAll();
    }

    @AfterAll
    public static void afterAll(@Autowired UserRepository userRepository,
                                @Autowired RoleRepository roleRepository) {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("findById returns user when successful")
    void shouldFindUserByIdAndFetchRoles() {
        // given
        // create role user
        Role roleUser = Role.builder().name(ERole.ROLE_USER).build();
        roleRepository.save(roleUser);
        // create user
        String username = "username";
        User user = User.builder()
                .username(username)
                .firstName("firstName")
                .password("password")
                .roles(Set.of(roleUser))
                .build();
        User savedUser = underTest.save(user);

        // when
        Optional<User> optionalUser = underTest.findById(savedUser.getId());

        // then
        assertThat(optionalUser).isPresent();

        User userFound = optionalUser.get();

        assertThat(userFound).isEqualTo(user);
        assertThat(userFound.getUsername()).isEqualTo(username);
        assertThat(userFound.getRoles()).hasSize(1).contains(roleUser);
    }

    @Test
    @DisplayName("findByUsername returns user when successful")
    void shouldFindUserByUsernameAndFetchRoles() {
        // given
        // create role user
        Role roleUser = Role.builder().name(ERole.ROLE_USER).build();
        roleRepository.save(roleUser);
        // create user
        String username = "username";
        User user = User.builder()
                .username(username)
                .firstName("firstName")
                .password("password")
                .roles(Set.of(roleUser))
                .build();
        underTest.save(user);

        // when
        Optional<User> optionalUser = underTest.findByUsername(username);

        // then
        assertThat(optionalUser).isPresent();

        User userFound = optionalUser.get();

        assertThat(userFound).isEqualTo(user);
        assertThat(userFound.getUsername()).isEqualTo(username);
        assertThat(userFound.getRoles()).hasSize(1).contains(roleUser);
    }

    @Test
    @DisplayName("existsByUsername returns true when username already exists")
    void shouldReturnTrueWhenUsernameAlreadyExists() {
        // given
        // create role user
        Role roleUser = Role.builder().name(ERole.ROLE_USER).build();
        roleRepository.save(roleUser);
        // create user
        String username = "username";
        User user = User.builder()
                .username(username)
                .firstName("firstName")
                .password("password")
                .roles(Set.of(roleUser))
                .build();
        underTest.save(user);

        // when
        boolean exists = underTest.existsByUsername(username);

        // then
        assertThat(exists).isTrue();
    }
}