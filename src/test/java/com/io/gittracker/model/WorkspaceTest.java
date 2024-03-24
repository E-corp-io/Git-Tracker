package com.io.gittracker.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class WorkspaceTest {

    @Mock
    private GithubRepository githubRepository1;

    @Mock
    private GithubRepository githubRepository2;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldAddAllRepositories() {
        when(githubRepository1.getId()).thenReturn(1L);
        when(githubRepository2.getId()).thenReturn(2L);

        Workspace workspace = new Workspace("TEST WORKSPACE");
        workspace.addRepositoryToDefaultGroup(githubRepository1);
        workspace.addRepositoryToDefaultGroup(githubRepository2);

        assertEquals(2, workspace.getAllRepositories().size());
    }
}
