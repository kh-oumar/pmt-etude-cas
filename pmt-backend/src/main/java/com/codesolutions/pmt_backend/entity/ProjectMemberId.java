package com.codesolutions.pmt_backend.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectMemberId implements Serializable {

    private Long userId;
    private Long projectId;

    public ProjectMemberId() {
    }

    public ProjectMemberId(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMemberId)) return false;
        ProjectMemberId that = (ProjectMemberId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId);
    }
}
