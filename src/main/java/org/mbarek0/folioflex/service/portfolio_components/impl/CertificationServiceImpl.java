package org.mbarek0.folioflex.service.portfolio_components.impl;

import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Certification;
import org.mbarek0.folioflex.repository.CertificationRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.CertificationService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs.*;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.CertificationRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class CertificationServiceImpl implements CertificationService {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final UserService userService;
    private final CertificationRepository certificationRepository;
    private final S3Service s3Service;
    private final AuthenticationService authenticationService;

    @Override
    public List<Certification> createCertification(List<CertificationRequestVM> request, MultipartFile certificationImageFile) {
        User user = authenticationService.getAuthenticatedUser();

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserNotFoundException("User does not have any languages");

        if (userLanguageCount != request.size()) throw new InvalidCertificationDataException("Number of languages in request does not match user's languages");

        UUID certificationId = getUniqueCertificationId();
        String certificationImageUrl = s3Service.uploadFile(certificationImageFile);

        int displayOrder = certificationRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : certificationRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        int expectedSize = List.of(request.get(0).getSkills()).size();

        boolean isValid = request.stream()
                .skip(1)
                .allMatch(r -> List.of(r.getSkills()).size() == expectedSize);

        if (!isValid) {
            throw new InvalidCertificationDataException("Number of skills in request must be the same for all languages");
        }
        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveCertification(req, user, lang, certificationId, certificationImageUrl, displayOrder);
                })
                .toList();
    }

    private Certification saveCertification(CertificationRequestVM request, User user, Language lang, UUID certificationId, String certificationImageUrl, int displayOrder) {
        if (!Objects.equals(request.getUserId(), user.getId())) {
            throw new CertificationNotBelongToUserException("Certification does not belong to user");
        }

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (certificationRepository.existsByUserAndLanguageAndCertificationIdAndIsDeletedFalseAndIsArchivedFalse(user, lang, certificationId)) {
            throw new CertificationAlreadyExistsException(
                    "Certification already exists for this language: "
                            + lang.getLanguage() + "(" + lang.getCode() + ") And this certification Id:"
                            + certificationId);
        }

        Certification certification = new Certification();
        certification.setCertificationId(certificationId);
        certification.setUser(user);
        certification.setLanguage(lang);
        certification.setCertificationName(request.getCertificationName());
        certification.setIssuingOrganization(request.getIssuingOrganization());
        certification.setCertificationImage(certificationImageUrl);
        certification.setIssueDate(request.getIssueDate());
        certification.setExpirationDate(request.getExpirationDate());
        certification.setDisplayOrder(displayOrder);
        certification.setCreatedAt(LocalDateTime.now());
        certification.setUpdatedAt(LocalDateTime.now());
        certification.setArchived(false);
        certification.setDeleted(false);
        if (request.getSkills() != null) {
            certification.setSkills(List.of(request.getSkills()));
        }

        return certificationRepository.save(certification);
    }

    private UUID getUniqueCertificationId() {
        UUID certificationId = UUID.randomUUID();
        while (certificationRepository.existsByCertificationId(certificationId)) {
            certificationId = UUID.randomUUID();
        }
        return certificationId;
    }

    @Override
    public List<Certification> getAllCertifications(String username, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return certificationRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, language);
    }

    @Override
    public List<Certification> getAllCertifications(String username, UUID certificationId) {
        User user = userService.findByUsername(username);

        List<Certification> certifications =  certificationRepository.findAllByUserAndCertificationIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, certificationId);
        if (certifications.isEmpty())
            throw new CertificationNotFoundException("Certification not found with certification ID: " + certificationId);
        return certifications;
    }

    @Override
    public Certification getCertification(String username, UUID uuid, String languageCode) {
        User user = userService.findByUsername(username);
        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return certificationRepository.findByUserAndCertificationIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new CertificationNotFoundException("Certification not found"));
    }

    @Override
    public List<Certification> updateCertification(UUID uuid, List<CertificationRequestVM> certificationVM, MultipartFile certificationImageFile) {
        User user = authenticationService.getAuthenticatedUser();


        if (certificationVM.isEmpty())
            throw new InvalidCertificationDataException("Certification data is empty");

        if (certificationVM.get(0).getCertificationId() == null)
            throw new InvalidCertificationDataException("Certification ID is required");

        if (certificationVM.stream().anyMatch(req -> !req.getCertificationId().equals(uuid)))
            throw new InvalidCertificationDataException("Certification ID in request does not match the path");

        if (certificationVM.size() != new HashSet<>(certificationVM).size())
            throw new InvalidCertificationDataException("Duplicate languages in request");

        if (certificationVM.size() != portfolioTranslationLanguageService.findLanguagesCountByUserId(certificationVM.get(0).getUserId()))
            throw new InvalidCertificationDataException("Number of languages in request does not match user's languages");

        List<Certification> certifications = certificationRepository.findAllByCertificationIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (certifications.isEmpty())
            throw new CertificationNotFoundException("Certification not found with certification ID: " + uuid);

        int expectedSize = List.of(certificationVM.get(0).getSkills()).size();

        boolean isValid = certificationVM.stream()
                .skip(1)
                .allMatch(r -> List.of(r.getSkills()).size() == expectedSize);

        if (!isValid) {
            throw new InvalidCertificationDataException("Number of skills in request must be the same for all languages");
        }

        certifications.forEach(certification -> {
            CertificationRequestVM request = certificationVM.stream()
                    .filter(req -> req.getLanguageCode().equals(certification.getLanguage().getCode()))
                    .findFirst()
                    .orElseThrow(() -> new CertificationNotFoundException("Certification not found for language: " + certification.getLanguage().getLanguage()));

            String certificationImageUrl = certificationImageFile == null ? certification.getCertificationImage() : s3Service.uploadFile(certificationImageFile);

            if (!Objects.equals(request.getUserId(), user.getId())) {
                throw new CertificationNotBelongToUserException("Certification does not belong to user");
            }

            certification.setCertificationName(request.getCertificationName());
            certification.setIssuingOrganization(request.getIssuingOrganization());
            certification.setCertificationImage(certificationImageUrl);
            certification.setIssueDate(request.getIssueDate());
            certification.setExpirationDate(request.getExpirationDate());
            if (request.getSkills() != null)
                certification.setSkills(new ArrayList<>(List.of(request.getSkills())));
            certification.setUpdatedAt(LocalDateTime.now());
        });

        return certificationRepository.saveAll(certifications);
    }

    @Override
    public List<Certification> reorder(List<ReorderRequest> reorderRequests) {
        List<Integer> displayOrders = reorderRequests.stream()
                .map(ReorderRequest::getDisplayOrder)
                .toList();
        if (displayOrders.size() != new HashSet<>(displayOrders).size())
            throw new InvalidCertificationDataException("Duplicate display orders in request");

        if (reorderRequests.isEmpty())
            throw new InvalidCertificationDataException("Reorder request is empty");

        Language lang = portfolioTranslationLanguageService.getLanguageByCode(reorderRequests.get(0).getLanguageCode());

        return reorderRequests.stream()
                .map(req -> {
                    List<Certification> certifications = certificationRepository.findAllByCertificationIdAndIsDeletedFalseAndIsArchivedFalse(req.getComponentId());
                    if (certifications.isEmpty())
                        throw new CertificationNotFoundException("Certification not found with certification ID: " + req.getComponentId());

                    certifications.forEach(cert -> cert.setDisplayOrder(req.getDisplayOrder()));

                    certificationRepository.saveAll(certifications);

                    return certifications
                            .stream()
                            .filter(cert -> cert.getLanguage().equals(lang))
                            .findFirst()
                            .orElseThrow(() -> new CertificationNotFoundException("Certification not found for language: " + lang.getLanguage()));
                })
                .toList();
    }

    @Override
    public List<Certification> deleteCertification(UUID uuid) {
        List<Certification> certifications = certificationRepository.findAllByCertificationIdAndIsDeletedFalse(uuid);

        User user = authenticationService.getAuthenticatedUser();

        if (certifications.isEmpty())
            throw new CertificationNotFoundException("Certification not found with certification ID: " + uuid);

        if (certifications.stream().anyMatch(cert -> !cert.getUser().equals(user)))
            throw new CertificationNotBelongToUserException("Certification does not belong to user");

        certifications.forEach(certification -> {
            certification.setDeleted(true);
            certification.setUpdatedAt(LocalDateTime.now());
        });

        return certificationRepository.saveAll(certifications);
    }

    @Override
    public List<Certification> archiveCertification(UUID uuid) {
        List<Certification> certifications = certificationRepository.findAllByCertificationIdAndIsDeletedFalseAndIsArchivedFalse(uuid);

        if (certifications.isEmpty())
            throw new CertificationNotFoundException("Certification not found with certification ID: " + uuid);

        certifications.forEach(certification -> {
            certification.setArchived(true);
            certification.setUpdatedAt(LocalDateTime.now());
        });

        return certificationRepository.saveAll(certifications);
    }
}