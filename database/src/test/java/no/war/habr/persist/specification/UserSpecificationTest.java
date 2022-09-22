package no.war.habr.persist.specification;

import lombok.extern.slf4j.Slf4j;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-tc.properties")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
class UserSpecificationTest {

    @Autowired
    private UserRepository underTest;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        topicRepository.deleteAll();
        tagRepository.deleteAll();
        underTest.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldFindUsersByUsernamePatternLike() {
        // given
        // create search pattern
        String usernamePattern = "shUriC";
        Specification<User> spec = Specification
                .where(UserSpecification.usernameLike(usernamePattern));

        // create user 1
        User user1 = User.builder()
                .username("shuricans")
                .firstName("name_1")
                .password("password")
                .build();
        underTest.save(user1);

        // create user 2
        User user2 = User.builder()
                .username("PepeAwesomeFrog")
                .firstName("name_2")
                .password("password")
                .build();
        underTest.save(user2);

        // create user 3
        User user3 = User.builder()
                .username("lopushuric")
                .firstName("name_3")
                .password("password")
                .build();
        underTest.save(user3);

        // when
        List<User> users = underTest.findAll(spec);

        // then
        assertThat(users)
                .hasSize(2)
                .contains(user1, user3);
    }

    @Test
    void shouldFindUsersByFirstNamePatternLike() {
        // given
        // create search pattern
        String firstNamePattern = "ash";
        Specification<User> spec = Specification
                .where(UserSpecification.firstNameLike(firstNamePattern));

        // create user 1
        User user1 = User.builder()
                .username("username_1")
                .firstName("Dasha")
                .password("password")
                .build();
        underTest.save(user1);

        // create user 2
        User user2 = User.builder()
                .username("username_2")
                .firstName("Masha")
                .password("password")
                .build();
        underTest.save(user2);

        // create user 3
        User user3 = User.builder()
                .username("username_3")
                .firstName("Tom")
                .password("password")
                .build();
        underTest.save(user3);

        // when
        List<User> users = underTest.findAll(spec);

        // then
        assertThat(users)
                .hasSize(2)
                .contains(user1, user2);
    }

    @Test
    void shouldFindUsersByLastNameLikePatternLike() {
        // given
        // create search pattern
        String lastNamePattern = "rov";
        Specification<User> spec = Specification
                .where(UserSpecification.lastNameLike(lastNamePattern));

        // create user 1
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .lastName("Petrov")
                .password("password")
                .build();
        underTest.save(user1);

        // create user 2
        User user2 = User.builder()
                .username("username_2")
                .firstName("name_2")
                .lastName("Sidorov")
                .password("password")
                .build();
        underTest.save(user2);

        // create user 3
        User user3 = User.builder()
                .username("username_3")
                .firstName("name_3")
                .lastName("Ivanov")
                .password("password")
                .build();
        underTest.save(user3);

        // when
        List<User> users = underTest.findAll(spec);

        // then
        assertThat(users)
                .hasSize(2)
                .contains(user1, user2);
    }

    @Test
    void shouldFindUsersByCondition() {
        // given
        Specification<User> spec = Specification
                .where(UserSpecification.condition(EUserCondition.ACTIVE));

        // create user 1
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .condition(EUserCondition.NOT_ACTIVE)
                .build();
        underTest.save(user1);

        // create user 2
        User user2 = User.builder()
                .username("username_2")
                .firstName("name_2")
                .password("password")
                .build();
        underTest.save(user2);

        // create user 3
        User user3 = User.builder()
                .username("username_3")
                .firstName("name_3")
                .password("password")
                .build();
        underTest.save(user3);

        // when
        List<User> users = underTest.findAll(spec);

        // then
        assertThat(users)
                .hasSize(2)
                .contains(user2, user3);
    }

    @Test
    void shouldFindAllUsersAndFetchRoles() {
        // given
        Specification<User> spec = Specification
                .where(UserSpecification.fetchRoles());

        // create role user
        Role roleUser = Role.builder().name(ERole.ROLE_USER).build();
        roleRepository.save(roleUser);

        // create user 1
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .roles(Collections.singleton(roleUser))
                .build();
        underTest.save(user1);

        // create user 2
        User user2 = User.builder()
                .username("username_2")
                .firstName("name_2")
                .password("password")
                .roles(Collections.singleton(roleUser))
                .build();
        underTest.save(user2);

        // create user 3 without roles
        User user3 = User.builder()
                .username("username_3")
                .firstName("name_3")
                .password("password")
                .build();
        underTest.save(user3);

        // when
        List<User> users = underTest.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));

        // then
        assertThat(users)
                .hasSize(3)
                .contains(user1, user2, user3);

        // check fetched role
        // user1
        Set<Role> roles = users.get(0).getRoles();
        assertThat(roles)
                .hasSize(1)
                .contains(roleUser);

        // user2
        roles = users.get(1).getRoles();
        assertThat(roles)
                .hasSize(1)
                .contains(roleUser);

        // user3
        roles = users.get(2).getRoles();
        assertThat(roles).isEmpty();
    }

    @Test
    void shouldFindAllUsersAndFetchPosts() {
        // given
        Specification<User> spec = Specification
                .where(UserSpecification.fetchPosts());

        // create user
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .build();
        underTest.save(user1);

        // create topic
        Topic designTopic = Topic.builder().name("Design").build();
        topicRepository.save(designTopic);

        // create post
        Post post = Post.builder()
                .title("awesome title")
                .content("savage content")
                .owner(user1)
                .topic(designTopic)
                .tags(Collections.singleton(Tag.builder().name("tag").build()))
                .build();

        postRepository.save(post);

        // when
        List<User> users = underTest.findAll(spec);

        // then
        assertThat(users)
                .hasSize(1)
                .contains(user1);

        List<Post> posts = users.get(0).getPosts();
        assertThat(posts)
                .hasSize(1)
                .contains(post);
    }
}