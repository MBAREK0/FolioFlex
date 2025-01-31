package org.mbarek0.folioflex.service.translation.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.repository.LanguageRepository;
import org.mbarek0.folioflex.repository.PortfolioTranslationLanguageRepository;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.InvalidInputException;
import org.mbarek0.folioflex.web.exception.translationExs.EnglishLanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.LanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.PortfolioTranslationLanguageAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.CreatePortfolioTranslationLanguageVM;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfolioTranslationLanguageServiceImpl implements PortfolioTranslationLanguageService {

    private final PortfolioTranslationLanguageRepository portfolioTranslationLanguageRepository;
    private final LanguageRepository languageRepository;
    private final UserService userService;

    @Override
    public List<PortfolioTranslationLanguage> save(List<CreatePortfolioTranslationLanguageVM> createPortfolioTranslationLanguageVMs) {

        if (createPortfolioTranslationLanguageVMs == null) {
            throw new InvalidInputException("createPortfolioTranslationLanguageVMs is null");
        }

        if (portfolioTranslationLanguageRepository.findAll().toArray().length > 0) {
            throw new InvalidInputException("Portfolio translation languages already exist try to insert one by one");
        }
        List<Language> languages = createPortfolioTranslationLanguageVMs.stream()
                .map(vm -> languageRepository.findById(vm.getLanguageId())
                        .orElseThrow(() -> new LanguageNotFoundException(vm.getLanguageId())))
                .collect(Collectors.toList());

        boolean isEnglishInList = languages.stream()
                .anyMatch(language -> "en".equals(language.getCode()));

        if (!isEnglishInList) {
            Language englishLanguage = languageRepository.findByCode("en")
                    .orElseThrow(EnglishLanguageNotFoundException::new);

            languages.add(englishLanguage);

            createPortfolioTranslationLanguageVMs.add(new CreatePortfolioTranslationLanguageVM(
                    englishLanguage.getId(),
                    createPortfolioTranslationLanguageVMs.get(0).getUserId()
            ));
        }

        return createPortfolioTranslationLanguageVMs.stream()
                .map(vm -> {
                    Language language = languages.stream()
                            .filter(l -> Objects.equals(l.getId(), vm.getLanguageId()))
                            .findFirst()
                            .orElseThrow(() -> new LanguageNotFoundException(vm.getLanguageId()));

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

    @Override
    public PortfolioTranslationLanguage save(Long userId, Long languageId) {
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        User user = userService.findUserById(userId);

        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        if (portfolioTranslationLanguageRepository.existsByLanguageAndUser(language, user)) {
            throw new PortfolioTranslationLanguageAlreadyExistsException(userId);
        }

        PortfolioTranslationLanguage pTL = PortfolioTranslationLanguage.builder()
                .language(language)
                .user(user)
                .isPrimary(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return portfolioTranslationLanguageRepository.save(pTL);
    }

    @Override
    public List<PortfolioTranslationLanguage> getAllPortfolioTranslationLanguagesForUser(Long userId) {
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException("User with ID: " + userId + " not found");
        }

        User user = userService.findUserById(userId);

        return portfolioTranslationLanguageRepository.findByUser(user);
    }

    @Override
    public List<Language> getAllPortfolioTranslationLanguage() {
        return languageRepository.findAll();
    }

    @Override
    public PortfolioTranslationLanguage updatePortfolioTranslationLanguagePrimary(Long userId, Long languageId) {
        User user = userService.findUserById(userId);
        Language language = languageRepository.findById(languageId)
                .orElseThrow(() -> new LanguageNotFoundException(languageId));

        PortfolioTranslationLanguage pTL = portfolioTranslationLanguageRepository
                .findByUserAndLanguage(user, language);

        if (pTL == null) {
            throw new InvalidInputException("Portfolio translation language not found for user with ID: " + userId + " and language ID: " + languageId);
        }

        PortfolioTranslationLanguage pTLPrimary = portfolioTranslationLanguageRepository.findByisPrimary(true);
        if (pTLPrimary != null) {
            pTLPrimary.setPrimary(false);
            portfolioTranslationLanguageRepository.save(pTLPrimary);
        }

        pTL.setPrimary(true);
        return portfolioTranslationLanguageRepository.save(pTL);
    }

    @Override
    public void deletePortfolioTranslationLanguage(Long userId, Long languageId) {
        //TODO: Implement this method
    }
}