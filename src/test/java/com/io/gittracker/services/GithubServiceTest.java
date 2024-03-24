package com.io.gittracker.services;

import static org.junit.jupiter.api.Assertions.*;

import com.io.gittracker.model.GithubRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GithubServiceTest {

    @Autowired
    private GithubService githubService;

    @Test
    @Disabled("This test requires credentials that are not yet present on github CI")
    void shouldGetPublicRepoData() {
        GithubRepository repository = githubService.getRepository("E-corp-io/GIT-TRACKER-API-TESTS");
        System.out.println(repository.getName());
        assertEquals("GIT-TRACKER-API-TESTS", repository.getName());
        assertEquals(
                "https://github.com/E-corp-io/GIT-TRACKER-API-TESTS",
                repository.getHtmlUrl().toString());
    }
}
