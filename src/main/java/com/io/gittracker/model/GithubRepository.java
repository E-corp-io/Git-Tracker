package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;
import com.io.gittracker.utils.GHMapper;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubRepository implements Refreshable, Serializable {
    /** id used by GitHub api */
    private final long id;

    private URL htmlUrl;
    private String name;
    private final Map<Long, PullRequest> pullRequestsMap = new HashMap<>();
    private final List<String> labels = new ArrayList<>();
    private Group workspaceGroup; // TODO: remove
    transient Logger logger = LoggerFactory.getLogger(GithubRepository.class);

    private final transient GithubService githubService;

    public GithubRepository(GithubService githubService, long id) {
        this.id = id;
        this.githubService = githubService;
        refresh();
    }

    public List<PullRequest> getPullRequests() {
        return pullRequestsMap.values().stream().toList();
    }

    void mergePullRequests(List<PullRequest> newPullRequests) {
        for (PullRequest pr : newPullRequests) {
            Long prId = pr.getId();
            if (pullRequestsMap.containsKey(prId)) {
                pullRequestsMap.get(prId).update(pr);
            } else {
                pullRequestsMap.put(prId, pr);
            }
        }
    }

    public void setWorkspaceGroup(Group workspaceGroup) {
        this.workspaceGroup = workspaceGroup;
    }

    List<PullRequest> fetchPullRequests() {
        try {
            List<GHPullRequest> ghPullRequests =
                    githubService.getRepositoryById(id).getPullRequests(GHIssueState.ALL);
            return ghPullRequests.stream()
                    .map(pr -> GHMapper.mapToPullRequest(githubService, pr))
                    .toList();
        } catch (IOException e) {
            logger.error("Failed to fetch repository: {}", name, e);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GithubRepository) obj;
        return Objects.equals(this.name, that.name) && Objects.equals(this.htmlUrl, that.htmlUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Repository[" + "githubName="
                + name + ", " + "url="
                + htmlUrl + ", " + "grade="
                + labels + ", " + "group=";
    }

    public String getName() {
        return name;
    }

    public URL getHtmlUrl() {
        return htmlUrl;
    }

    public long getId() {
        return id;
    }

    public Group getWorkspaceGroup() {
        return workspaceGroup;
    }

    public List<String> getLabels() {
        return labels;
    }

    @Override
    public void refresh() {
        GHRepository repository = githubService.getRepositoryById(id);
        this.htmlUrl = repository.getHtmlUrl();
        this.name = repository.getName();
        mergePullRequests(fetchPullRequests());
    }
}
