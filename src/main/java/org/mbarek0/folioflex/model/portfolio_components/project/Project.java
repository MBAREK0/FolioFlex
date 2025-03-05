package org.mbarek0.folioflex.model.portfolio_components.project;

import jakarta.persistence.*;
import lombok.*;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.enums.ProjectStatus;
import org.mbarek0.folioflex.model.language.Language;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID projectId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_code", referencedColumnName = "code", nullable = false)
    private Language language;


    @Column(nullable = false, length = 255)
    private String projectName;

    @Column(length = 255)
    private String projectLogo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 255)
    private String demoLink;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(nullable = false)
    private Integer orderDisplay;

    @Column(nullable = false, length = 50)
    private ProjectStatus status;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isArchived = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectScreenshot> screenshots;

    @ElementCollection
    @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill", nullable = true)
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag", nullable = true)
    private List<String> tags;
}
