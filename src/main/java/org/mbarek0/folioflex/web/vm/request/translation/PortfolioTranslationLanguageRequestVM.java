package org.mbarek0.folioflex.web.vm.request.translation;

import jakarta.validation.constraints.NotNull;

public class PortfolioTranslationLanguageRequestVM {

    @NotNull(message = "Language is required")
    private Long languageId;

    @NotNull(message = "User is required")
    private Long userId;

    // No-argument constructor (required for deserialization)
    public PortfolioTranslationLanguageRequestVM() {
    }

    // Parameterized constructor (optional, for convenience)
    public PortfolioTranslationLanguageRequestVM(Long languageId, Long userId) {
        this.languageId = languageId;
        this.userId = userId;
    }

    // Getters and setters (required for validation and serialization/deserialization)
    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Optional: Override toString() for better logging/debugging
    @Override
    public String toString() {
        return "CreatePortfolioTranslationLanguageVM{" +
                "languageId=" + languageId +
                ", userId=" + userId +
                '}';
    }
}