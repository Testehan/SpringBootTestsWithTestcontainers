package com.testehan.SpringBootTestsWithTestcontainers.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@DataJdbcTest       // will load the relevant parts for a spring Data test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // this is so that Spring does not look for h2 in memory DB in tests, and complain that it is not configure
public class PostRepositoryTest {

    @Container  // this is static, because we will just use 1 container for all tests, we don't need one for each test
    @ServiceConnection  // tells spring boot to configure itself in what concerns the DB connection details, if I understood right
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    PostRepository postRepository;

    @Autowired
    JdbcConnectionDetails jdbcConnectionDetails;

    @BeforeEach
    void setUp() {
        List<Post> posts = List.of(new Post(1,1,"Hello, World!", "This is my first post!",null));
        postRepository.saveAll(posts);
    }

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldReturnPostByTitle() {
        Post post = postRepository.findByTitle("Hello, World!").orElseThrow();
        assertEquals("Hello, World!", post.title(), "Post title should be 'Hello, World!'");
    }

    @Test
    void shouldNotReturnPostWhenTitleIsNotFound() {
        Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
        assertFalse(post.isPresent(), "Post should not be present");
    }
}