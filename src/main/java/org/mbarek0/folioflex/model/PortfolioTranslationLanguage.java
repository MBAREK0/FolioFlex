package org.mbarek0.folioflex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@Entity
@Table(name = "portfolio_translation_languages")
public class PortfolioTranslationLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isPrimary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
