package com.io.gittracker.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class Repository implements Serializable {
    private String githubName;
    private String url;
    private float grade;

    private String comment;
    private Date dueDate;
    private final List<String> labels;
    private int group;
    private int subgroup;

    public Repository(
            String githubName,
            String url,
            float grade,
            String comment,
            Date dueDate,
            List<String> labels,
            int group,
            int subgroup) {
        this.githubName = githubName;
        this.url = url;
        this.grade = grade;
        this.comment = comment;
        this.dueDate = dueDate;
        this.labels = labels;
        this.group = group;
        this.subgroup = subgroup;
    }

    public String getGithubName() {
        return githubName;
    }

    public String getUrl() {
        return url;
    }

    public float getGrade() {
        return grade;
    }

    public String getComment() {
        return comment;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public int getGroup() {
        return group;
    }

    public int getSubgroup() {
        return subgroup;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public void setGithubName(String githubName) {
        this.githubName = githubName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setSubgroup(int subgroup) {
        this.subgroup = subgroup;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Repository) obj;
        return Objects.equals(this.githubName, that.githubName)
                && Objects.equals(this.url, that.url)
                && Float.floatToIntBits(this.grade) == Float.floatToIntBits(that.grade)
                && Objects.equals(this.comment, that.comment)
                && Objects.equals(this.dueDate, that.dueDate)
                && Objects.equals(this.labels, that.labels)
                && this.group == that.group
                && this.subgroup == that.subgroup;
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubName, url, grade, comment, dueDate, labels, group, subgroup);
    }

    @Override
    public String toString() {
        return "Repository[" + "githubName="
                + githubName + ", " + "url="
                + url + ", " + "grade="
                + grade + ", " + "comment="
                + comment + ", " + "dueDate="
                + dueDate + ", " + "labels="
                + labels + ", " + "group="
                + group + ", " + "subgroup="
                + subgroup + ']';
    }
}
