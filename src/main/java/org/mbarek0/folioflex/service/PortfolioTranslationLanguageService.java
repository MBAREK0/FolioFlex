package org.mbarek0.folioflex.service;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.repository.LanguageRepository;
import org.mbarek0.folioflex.repository.PortfolioTranslationLanguageRepository;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.vm.request.CreatePortfolioTranslationLanguageVM;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioTranslationLanguageService {

    private final PortfolioTranslationLanguageRepository portfolioTranslationLanguageRepository;
    private final LanguageRepository languageRepository;
    private final UserService userService;

    public List<PortfolioTranslationLanguage> save(List<CreatePortfolioTranslationLanguageVM> createPortfolioTranslationLanguageVMs) {
        if (createPortfolioTranslationLanguageVMs == null) {
            throw new IllegalArgumentException("createPortfolioTranslationLanguageVMs is null");
        }

        List<Language> languages = createPortfolioTranslationLanguageVMs.stream()
                .map(vm -> languageRepository.findById(vm.getLanguageId())
                        .orElseThrow(() -> new IllegalArgumentException("Language not found with ID: " + vm.getLanguageId())))
                .collect(Collectors.toList());

        boolean isEnglishInList = languages.stream()
                .anyMatch(language -> "en".equals(language.getCode()));

        if (!isEnglishInList) {
            Language englishLanguage = languageRepository.findByCode("en")
                    .orElseThrow(() -> new IllegalArgumentException("English language not found in the database"));

            languages.add(englishLanguage);

            createPortfolioTranslationLanguageVMs.add(new CreatePortfolioTranslationLanguageVM(
                    englishLanguage.getId(),
                    createPortfolioTranslationLanguageVMs.get(0).getUserId()
            ));
        }

        return createPortfolioTranslationLanguageVMs.stream()
                .map(vm -> {
                    // Find the language by ID
                    Language language = languages.stream()
                            .filter(l -> Objects.equals(l.getId(), vm.getLanguageId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Language not found with ID: " + vm.getLanguageId()));

                    User user = userService.findUserById(vm.getUserId());

                    PortfolioTranslationLanguage pTL = PortfolioTranslationLanguage.builder()
                            .language(language)
                            .user(user)
                            .isPrimary("en".equals(language.getCode()))
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return portfolioTranslationLanguageRepository.save(pTL);
                })
                .collect(Collectors.toList());
    }


    public List<PortfolioTranslationLanguage> getAllPortfolioTranslationLanguagesForUser(Long userId) {
        // Check if the user exists
        if (!userService.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userService.findUserById(userId);

        // Fetch all portfolio translation languages for the user
        return portfolioTranslationLanguageRepository.findByUser(user);
    }
}