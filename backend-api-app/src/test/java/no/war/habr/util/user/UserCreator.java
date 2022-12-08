package no.war.habr.util.user;


import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import no.war.habr.persist.model.User;

import java.util.Set;

public class UserCreator {

    public static final Long ID = 1L;
    public static final String USERNAME = "shuricans";
    public static final String ADMIN = "admin";
    public static final String MODERATOR = "moderator";
    public static final String FIRSTNAME = "Sasha";
    public static final String PASSWORD = "password";
    public static final Set<Role> ROLES = Set.of(Role.builder().name(ERole.ROLE_USER).build());
    public static final Set<Role> ROLES_MOD = Set.of(Role.builder().name(ERole.ROLE_MODERATOR).build());
    public static final Set<Role> ROLES_ADMIN = Set.of(Role.builder().name(ERole.ROLE_ADMIN).build());

    public static User createUserToBeSave() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRSTNAME)
                .roles(ROLES)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .id(ID)
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRSTNAME)
                .roles(ROLES)
                .build();
    }

    public static User createModerator() {
        return User.builder()
                .id(ID)
                .username(MODERATOR)
                .password(PASSWORD)
                .firstName(FIRSTNAME)
                .roles(ROLES_MOD)
                .build();
    }

    public static User createAdmin() {
        return User.builder()
                .id(ID)
                .username(ADMIN)
                .password(PASSWORD)
                .firstName(FIRSTNAME)
                .roles(ROLES_ADMIN)
                .build();
    }
}
