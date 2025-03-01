package org.mbarek0.folioflex.model.portfolio_components;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.enums.IconType;
import org.mbarek0.folioflex.model.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID skillId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_code", referencedColumnName = "code", nullable = false)
    private Language language;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "icon_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IconType iconType;

    @Column(name = "icon_value", nullable = false)
    private String iconValue;

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

    @PrePersist
    public void prePersist() {
        if (skillId == null) {
            skillId = UUID.randomUUID();
        }
    }
}