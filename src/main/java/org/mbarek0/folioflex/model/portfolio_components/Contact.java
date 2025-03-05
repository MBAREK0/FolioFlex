package org.mbarek0.folioflex.model.portfolio_components;

import jakarta.persistence.*;
import lombok.*;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID contactId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_code", referencedColumnName = "code", nullable = false)
    private Language language;

    private String contactName;

    private String contactValue;

    private String contactType;

    private String iconPath;

    private Integer displayOrder;

    private boolean isDeleted = false;

    private boolean isArchived = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
