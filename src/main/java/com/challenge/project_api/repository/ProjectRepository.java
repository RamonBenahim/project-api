package com.challenge.project_api.repository;

import com.challenge.project_api.domain.entity.Project;
import com.challenge.project_api.domain.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();

    @Query("SELECT p.status, SUM(p.totalBudget) FROM Project p GROUP BY p.status")
    List<Object[]> sumBudgetByStatus();

    List<Project> findAllByStatus(ProjectStatus status);

    @Query("SELECT COUNT(DISTINCT m) FROM Project p JOIN p.members m")
    long countDistinctMembersInProjects();
}
