package com.io.gittracker.services;

import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.utils.GHMapper;
import java.io.IOException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GithubService {

    private GitHub gitHub;
    private final AppStateService appStateService;
    private final TokenService tokenService;

    private final Logger logger = LoggerFactory.getLogger(GithubService.class);

    public GithubService(GitHub gitHub, AppStateService appStateService, TokenService tokenService) {
        this.gitHub = gitHub;
        this.appStateService = appStateService;
        this.tokenService = tokenService;
    }
    public void setGitHub() throws IOException {
        this.gitHub = new GitHubBuilder().withOAuthToken(tokenService.getApiKey()).build();
    }

    /**
     *
     * Gets the repository object from 'owner/repo' string that GitHub calls as "repository name".
     * @param name repository name
     * @return repository
     */
    public GithubRepository getRepository(String name) {
        logger.debug("Fetching repository: {}", name);
        try {
            GithubRepository githubRepository = GHMapper.mapToRepo(this, gitHub.getRepository(name));
            logger.debug("Repository {} fetched successfully", githubRepository.getName());
            return githubRepository;
        } catch (IOException e) {
            logger.error("Failed to fetch repository: {}", name, e);
        }
        return null;
    }

    public GHRepository getRepositoryById(long id) {
        logger.debug("Fetching repository by id: {}", id);
        try {
            GHRepository ghRepository = gitHub.getRepositoryById(id);
            logger.debug("Repository {} fetched successfully", ghRepository.getName());
            return ghRepository;
        } catch (IOException e) {
            logger.error("Failed to fetch repository by id: {}", id, e);
        }
        return null;
    }

    public GitHub getGitHub() {
        return gitHub;
    }
}
