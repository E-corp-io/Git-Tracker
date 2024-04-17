package com.io.gittracker.utils;

import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.PRComment;
import com.io.gittracker.model.PullRequest;
import com.io.gittracker.services.GithubService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GHMapper {

    private final GithubService githubService;

    static Logger logger = LoggerFactory.getLogger(GHMapper.class);

    public GHMapper(GithubService githubService) {
        this.githubService = githubService;
    }

    public static GithubRepository mapToRepo(GithubService githubService, GHRepository ghRepository) {
        GithubRepository repo = new GithubRepository(ghRepository);
        // TODO: refresh repo on separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        repo.refresh(githubService, executorService);
        return repo;
    }

    public static PullRequest mapToPullRequest(GithubService githubService, GHPullRequest pullRequest) {
        try {
            PullRequest.PRStats prStats = new PullRequest.PRStats(
                    pullRequest.getChangedFiles(), pullRequest.getAdditions(), pullRequest.getDeletions());
            PullRequest pr = new PullRequest(
                    pullRequest.getId(),
                    null,
                    prStats,
                    pullRequest.getHtmlUrl(),
                    pullRequest.getCreatedAt(),
                    pullRequest.getUpdatedAt(),
                    pullRequest.getTitle(),
                    pullRequest.getState());

            pr.setCommentList(getComments(pullRequest));
            return pr;
        } catch (IOException e) {
            logger.warn("Failed to map pull request with id: {}", pullRequest.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private static List<PRComment> getComments(GHPullRequest pullRequest) throws IOException {
        return pullRequest.getComments().stream()
                .map(ghIssueComment -> {
                    try {
                        return new PRComment(ghIssueComment.getUser().getName(), ghIssueComment.getBody());
                    } catch (IOException e) {
                        logger.warn("failed to parse PRComment {}", ghIssueComment, e);
                    }
                    return null;
                })
                .toList();
    }
}
