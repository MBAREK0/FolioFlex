package org.mbarek0.folioflex.service.portfolio_components.impl;


import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Skill;
import org.mbarek0.folioflex.repository.SkillRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.portfolio_components.SkillService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.skillExs.InvalidSkillDataException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.SkillRequestVM;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final SkillRepository skillRepository;
    private final AuthenticationService authenticationService;

    @Override
    public List<Skill> createSkill(List<SkillRequestVM> request) {
        User user = authenticationService.getAuthenticatedUser();

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserDontHaveLanguageException("User does not have any languages");

        if (userLanguageCount != request.size()) throw new InvalidSkillDataException("Number of languages in request does not match user's languages");

        UUID skillId = getUniqueSkillId();

        int displayOrder = skillRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : skillRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        // check if the skill name is unique
        request.forEach(req -> {
                    if (skillRepository.existsByUserAndSkillNameAndIsDeletedFalseAndIsArchivedFalse(user, req.getSkillName()) )
                        throw new SkillAlreadyExistsException("Skill already exists for this skill name:" + req.getSkillName());
                });

        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveSkill(req, user, lang, skillId, displayOrder);
                })
                .toList();
    }

    private Skill saveSkill(SkillRequestVM request, User user, Language lang, UUID skillId, int displayOrder) {
        if (!Objects.equals(user.getId(), request.getUserId())) {
            throw new SkillNotBelongToUserException("Skill does not belong to user");
        }

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (skillRepository.existsByUserAndLanguageAndSkillIdAndIsDeletedFalseAndIsArchivedFalse(user, lang, skillId)) {
            throw new SkillAlreadyExistsException(
                    "Skill already exists for this language: "
                            + lang.getLanguage() + "(" + lang.getCode() + ") And this skill Id:"
                            + skillId );
        }



        Skill skill = new Skill();
        skill.setSkillId(skillId);
        skill.setUser(user);
        skill.setLanguage(lang);
        skill.setSkillName(request.getSkillName());
        skill.setIconType(request.getIconType());
        skill.setIconValue(request.getIconValue());
        skill.setDisplayOrder(displayOrder);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        skill.setArchived(false);
        skill.setDeleted(false);

        return skillRepository.save(skill);
    }

    private UUID getUniqueSkillId() {
        UUID skillId = UUID.randomUUID();
        while (skillRepository.existsBySkillId(skillId)) {
            skillId = UUID.randomUUID();
        }
        return skillId;
    }

    // --------------------------------------- Fetch Skills ---------------------------------------

    @Override
    public List<Skill> getAllSkills(String username, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return skillRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, language);
    }

    @Override
    public List<Skill> getAllSkills(String username, UUID skillId) {
        User user = userService.findByUsername(username);

        List<Skill> skills = skillRepository.findAllByUserAndSkillIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, skillId);
        if (skills.isEmpty())
            throw new SkillNotFoundException("Skill not found with skill ID: " + skillId);
        return skills;
    }

    @Override
    public Skill getSkill(String username, UUID uuid, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user):
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return skillRepository.findByUserAndSkillIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
    }

    // --------------------------------------- Update Skills ---------------------------------------

    @Override
    public List<Skill> updateSkill(UUID uuid, List<SkillRequestVM> skillVMs) {
        User user = authenticationService.getAuthenticatedUser();

        if (skillVMs.isEmpty())
            throw new InvalidSkillDataException("Skill data is empty");

        skillVMs.forEach(req -> {
            if (req.getSkillId() == null || req.getSkillId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                throw new InvalidSkillDataException("Skill ID is missing in request for language: " + req.getLanguageCode());
            }
            if (!req.getSkillId().equals(uuid)) {
                throw new InvalidSkillDataException("Skill ID in request does not match the path for language: " + req.getLanguageCode());
            }
        });

        if (skillVMs.get(0).getSkillId() == null)
            throw new InvalidSkillDataException("Skill ID is required");
        if (skillVMs.stream().anyMatch(req -> !req.getSkillId().equals(uuid)))
            throw new InvalidSkillDataException("Skill ID in request does not match the path");

        if (skillVMs.size() != new HashSet<>(skillVMs).size())
            throw new InvalidSkillDataException("Duplicate languages in request");

        if (skillVMs.size() != portfolioTranslationLanguageService.findLanguagesCountByUserId(skillVMs.get(0).getUserId()))
            throw new InvalidSkillDataException("Number of languages in request does not match user's languages");

        List<Skill> skills = skillRepository.findAllBySkillIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (skills.isEmpty())
            throw new SkillNotFoundException("Skill not found with skill ID: " + uuid);

        skills.forEach(skill -> {
            SkillRequestVM request = skillVMs.stream()
                    .filter(req -> req.getLanguageCode().equals(skill.getLanguage().getCode()))
                    .findFirst()
                    .orElseThrow(() -> new SkillNotFoundException("Skill not found for language: " + skill.getLanguage().getLanguage()));

            if (!Objects.equals(request.getUserId(), user.getId())) {
                throw new SkillNotBelongToUserException("Skill does not belong to user");
            }

            skill.setSkillName(request.getSkillName());
            skill.setIconType(request.getIconType());
            skill.setIconValue(request.getIconValue());
            skill.setUpdatedAt(LocalDateTime.now());
        });

        return skillRepository.saveAll(skills);
    }

    @Override
    public List<Skill> reorder(List<ReorderRequest> reorderRequests) {
        List<Integer> displayOrders = reorderRequests.stream()
                .map(ReorderRequest::getDisplayOrder)
                .toList();
        if (displayOrders.size() != new HashSet<>(displayOrders).size())
            throw new InvalidSkillDataException("Duplicate display orders in request");

        if (reorderRequests.isEmpty())
            throw new InvalidSkillDataException("Reorder request is empty");

        Language lang = portfolioTranslationLanguageService.getLanguageByCode(reorderRequests.get(0).getLanguageCode());

        return reorderRequests.stream()
                .map(req -> {
                    List<Skill> skills = skillRepository.findAllBySkillIdAndIsDeletedFalseAndIsArchivedFalse(req.getComponentId());
                    if (skills.isEmpty())
                        throw new SkillNotFoundException("Skill not found with skill ID: " + req.getComponentId());

                    skills.forEach(skill -> skill.setDisplayOrder(req.getDisplayOrder()));

                    skillRepository.saveAll(skills);

                    return skills
                            .stream()
                            .filter(s -> s.getLanguage().equals(lang))
                            .findFirst()
                            .orElseThrow(() -> new SkillNotFoundException("Skill not found for language: " + lang.getLanguage()));
                })
                .toList();
    }

    // --------------------------------------- Delete & archive Skills ---------------------------------------

    @Override
    public List<Skill> deleteSkill(UUID uuid) {

        User user = authenticationService.getAuthenticatedUser();

        List<Skill> skills = skillRepository.findAllBySkillIdAndIsDeletedFalse(uuid);

        if (skills.isEmpty())
            throw new SkillNotFoundException("Skill not found with skill ID: " + uuid);

        if (skills.stream().anyMatch(skill -> !skill.getUser().equals(user) ))
            throw new SkillNotBelongToUserException("Skill does not belong to user");

        skills.forEach(skill -> {
            skill.setDeleted(true);
            skill.setUpdatedAt(LocalDateTime.now());
        });

        return skillRepository.saveAll(skills);
    }

    @Override
    public List<Skill> archiveSkill(UUID uuid) {
        // mark as archived
        List<Skill> skills = skillRepository.findAllBySkillIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (skills.isEmpty())
            throw new SkillNotFoundException("Skill not found with skill ID: " + uuid);

        skills.forEach(skill -> {
            skill.setArchived(true);
            skill.setUpdatedAt(LocalDateTime.now());
        });

        return skillRepository.saveAll(skills);
    }
}