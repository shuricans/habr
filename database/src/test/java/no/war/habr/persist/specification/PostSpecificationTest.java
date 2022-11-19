package no.war.habr.persist.specification;

import lombok.extern.slf4j.Slf4j;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.PostRepository;
import no.war.habr.persist.repository.TagRepository;
import no.war.habr.persist.repository.TopicRepository;
import no.war.habr.persist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
class PostSpecificationTest {

    @Autowired
    private PostRepository underTest;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        topicRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindPostsByTopic() {
        // given
        // create search pattern
        String topicName = "Design";
        Specification<Post> spec = Specification
                .where(PostSpecification.topic(topicName));


        // create user
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .build();
        userRepository.save(user1);

        // create topic
        Topic designTopic = Topic.builder().name(topicName).build();
        topicRepository.save(designTopic);

        // create post
        Post post = Post.builder()
                .title("awesome title")
                .content("savage content")
                .owner(user1)
                .description("description")
                .topic(designTopic)
                .tags(Collections.singleton(Tag.builder().name("tag").build()))
                .build();

        underTest.save(post);

        // when
        List<Post> posts = underTest.findAll(spec);

        // then
        assertThat(posts)
                .hasSize(1)
                .contains(post);
    }

    @Test
    void shouldFindPostsByTag() {
        // given
        // create search pattern
        String tagName = "tag";
        String tagName2 = "tag2";
        Specification<Post> spec = Specification
                .where(PostSpecification.hasTags(List.of(tagName, tagName2)));


        // create user
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .build();
        userRepository.save(user1);

        // create topic
        Topic designTopic = Topic.builder().name("Design").build();
        topicRepository.save(designTopic);

        // create post with tag
        Tag tag = Tag.builder().name(tagName).build();
        Post postWithTag = Post.builder()
                .title("awesome title_1")
                .content("savage content_1")
                .owner(user1)
                .topic(designTopic)
                .description("description")
                .tags(Collections.singleton(tag))
                .build();

        underTest.save(postWithTag);

        // create post without tag
        Post postWithoutTag = Post.builder()
                .title("awesome title_2")
                .content("savage content_2")
                .owner(user1)
                .description("description")
                .topic(designTopic)
                .build();

        underTest.save(postWithoutTag);

        // when
        List<Post> posts = underTest.findAll(spec);

        // then
        assertThat(posts)
                .hasSize(1)
                .contains(postWithTag);
    }

    @Test
    void shouldFindPostsByCondition() {
        // given
        Specification<Post> spec = Specification
                .where(PostSpecification.condition(EPostCondition.DRAFT));

        // create user
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .build();
        userRepository.save(user1);

        // create topic
        Topic designTopic = Topic.builder().name("Design").build();
        topicRepository.save(designTopic);

        // create post
        Post post = Post.builder()
                .title("awesome title")
                .content("savage content")
                .owner(user1)
                .description("description")
                .topic(designTopic)
                .tags(Collections.singleton(Tag.builder().name("tag").build()))
                .build();

        underTest.save(post);

        // when
        List<Post> posts = underTest.findAll(spec);

        // then
        assertThat(posts)
                .hasSize(1)
                .contains(post);
    }

    @Test
    void shouldFindPostsAndFetchTags() {
        // given
        Specification<Post> spec = Specification
                .where(PostSpecification.fetchTags());

        // create user
        User user1 = User.builder()
                .username("username_1")
                .firstName("name_1")
                .password("password")
                .build();
        userRepository.save(user1);

        // create topic
        Topic designTopic = Topic.builder().name("Design").build();
        topicRepository.save(designTopic);

        // create post
        Tag tag = Tag.builder().name("tag").build();
        Post post = Post.builder()
                .title("awesome title")
                .content("savage content")
                .owner(user1)
                .description("description")
                .topic(designTopic)
                .tags(Collections.singleton(tag))
                .build();

        underTest.save(post);

        // when
        List<Post> posts = underTest.findAll(spec);

        // then
        assertThat(posts)
                .hasSize(1)
                .contains(post);

        Set<Tag> tags = posts.get(0).getTags();
        assertThat(tags)
                .hasSize(1)
                .contains(tag);
    }
}