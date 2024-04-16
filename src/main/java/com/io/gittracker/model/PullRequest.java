package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class PullRequest implements Serializable {

    /** id used by GitHub api */
    private long id;

    private List<PRComment> commentList;
    private PRStats prStats;
    private Grade grade;
    private boolean closed;
    private URL htmlURL;
    private final Date createdAtDate;
    private Date updatedAtDate;
    private String Title;

    private transient GithubService githubService;

    public String getTitle() {
        return Title;
    }

    public PullRequest(
            long id,
            List<PRComment> commentList,
            PRStats prStats,
            URL htmlURL,
            Date createdAtDate,
            Date updatedAtDate,
            String Title,
            boolean isClosed,
            GithubService githubService) {
        this.id = id;
        this.commentList = commentList;
        this.prStats = prStats;
        this.htmlURL = htmlURL;
        this.createdAtDate = createdAtDate;
        this.updatedAtDate = updatedAtDate;
        this.Title = Title;
        this.githubService = githubService;
        this.closed = isClosed;
    }

    public List<PRComment> listReviewComments() {
        return commentList;
    }

    public boolean isClosed() {
        return closed;
    }

    public Date getCreatedAt() {
        return createdAtDate;
    }

    public URL getHtmlURL() {
        return htmlURL;
    }

    public int getLinesChanged() {
        return prStats.filesChanged();
    }

    public int getLinesAdded() {
        return prStats.linesAdded();
    }

    public int getLinesDeleted() {
        return prStats.linesDeleted();
    }

    public long getId() {
        return id;
    }

    public Date getCreatedAtDate() {
        return createdAtDate;
    }

    public Date getUpdatedAtDate() {
        return updatedAtDate;
    }

    public List<PRComment> getComments() {
        return commentList;
    }

    public void setCommentList(List<PRComment> commentList) {
        this.commentList = commentList;
    }

    public void update(PullRequest pr) {
        this.prStats = pr.prStats;
        this.updatedAtDate = pr.updatedAtDate;
        this.commentList = pr.commentList;
        this.closed = pr.isClosed();
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public record PRStats(int filesChanged, int linesAdded, int linesDeleted) implements Serializable {
        @Override
        public String toString() {
            return "PRStats{" + "files:" + filesChanged + " +" + linesAdded + " -" + linesDeleted + '}';
        }
    }

    @Override
    public String toString() {
        return "PullRequest{" + "id="
                + id + ", prStats="
                + prStats + ", htmlURL="
                + htmlURL + ", createdAtDate="
                + createdAtDate + '}';
    }

    public enum IssueState {
        OPEN,
        CLOSED,
        ALL
    }
}
