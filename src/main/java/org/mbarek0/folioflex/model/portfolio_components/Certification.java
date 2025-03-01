package org.mbarek0.folioflex.model.portfolio_components;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID certificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_code", referencedColumnName = "code", nullable = false)
    private Language language;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "issuing_organization", nullable = false)
    private String issuingOrganization;

    @Column(name = "certification_image")
    private String certificationImage;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ElementCollection
    @CollectionTable(name = "certification_skills", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "skill", nullable = true)
    private List<String> skills;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "is_archived", nullable = false)
    private boolean isArchived;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
