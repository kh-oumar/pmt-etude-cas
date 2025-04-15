package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.ProjectMember;
import com.codesolutions.pmt_backend.entity.ProjectMemberId;
import com.codesolutions.pmt_backend.entity.Project;
import com.codesolutions.pmt_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);

    List<ProjectMember> findByUser(User user);
    List<ProjectMember> findByProject(Project project);
}
