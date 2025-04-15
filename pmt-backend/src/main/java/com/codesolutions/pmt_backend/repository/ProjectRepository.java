package com.codesolutions.pmt_backend.repository;

import com.codesolutions.pmt_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Recherche de projets par nom
    List<Project> findByName(String name);

    // Recherche de projets qui contiennent une chaîne dans leur nom (pour la recherche textuelle)
    List<Project> findByNameContaining(String keyword);
}
