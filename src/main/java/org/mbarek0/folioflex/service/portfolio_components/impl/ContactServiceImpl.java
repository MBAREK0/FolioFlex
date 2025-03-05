package org.mbarek0.folioflex.service.portfolio_components.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.Contact;
import org.mbarek0.folioflex.repository.ContactRepository;
import org.mbarek0.folioflex.service.authentication.AuthenticationService;
import org.mbarek0.folioflex.service.aws.S3Service;
import org.mbarek0.folioflex.service.portfolio_components.ContactService;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.service.user.UserService;
import org.mbarek0.folioflex.web.exception.portfolioExs.contactExs.ContactAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.contactExs.ContactNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.portfolioExs.contactExs.ContactNotFoundException;
import org.mbarek0.folioflex.web.exception.portfolioExs.contactExs.InvalidContactDataException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ContactRequestVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ContactServiceImpl implements ContactService {

    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;
    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final ContactRepository contactRepository;
    private final S3Service s3Service;
    private final UserService userService;

    @Override
    public List<Contact> createContact(List<ContactRequestVM> request, MultipartFile profilePictureFile) {
        User user = authenticationService.getAuthenticatedUser();

        Long userLanguageCount = portfolioTranslationLanguageService.findLanguagesCountByUserId(user.getId());

        if (userLanguageCount == 0) throw new UserDontHaveLanguageException("User does not have any languages");

        if (userLanguageCount != request.size())
            throw new InvalidContactDataException("Number of languages in request does not match user's languages");

        UUID contactId = getUniqueContactId();
        String profilePictureUrl = s3Service.uploadFile(profilePictureFile);

        int displayOrder = contactRepository.findAllByUserAndIsDeletedFalseAndIsArchivedFalse(user).isEmpty() ?
                0 : contactRepository.findMaxDisplayOrderByUserAndIsDeletedFalseAndIsArchivedFalse(user) + 1;

        return request.stream()
                .map(req -> {
                    Language lang = portfolioTranslationLanguageService.getLanguageByCode(req.getLanguageCode());
                    return saveContact(req, user, lang, contactId, profilePictureUrl, displayOrder);
                })
                .toList();
    }

    private Contact saveContact(ContactRequestVM request, User user, Language lang, UUID contactId, String profilePictureUrl, int displayOrder) {
        if (!Objects.equals(user.getId(), request.getUserId())) {
            throw new ContactNotBelongToUserException("Contact does not belong to user");
        }

        if (!portfolioTranslationLanguageService.existsByUserAndLanguage(user, lang)) {
            throw new UserDontHaveLanguageException("User is not allowed to use this language");
        }

        if (contactRepository.existsByUserAndLanguageAndContactIdAndIsDeletedFalseAndIsArchivedFalse(user, lang, contactId)) {
            throw new ContactAlreadyExistsException(
                    "Contact already exists for this language: "
                            + lang.getLanguage() + "(" + lang.getCode() + ") And this contact Id:"
                            + contactId);
        }

        Contact contact = new Contact();
        contact.setContactId(contactId);
        contact.setUser(user);
        contact.setLanguage(lang);
        contact.setContactName(request.getContactName());
        contact.setContactType(request.getContactType());
        contact.setContactValue(request.getContactValue());
        contact.setIconPath(profilePictureUrl);
        contact.setDisplayOrder(displayOrder);
        contact.setUpdatedAt(LocalDateTime.now());
        contact.setCreatedAt(LocalDateTime.now());
        contact.setArchived(false);
        contact.setDeleted(false);

        return contactRepository.save(contact);
    }

    private UUID getUniqueContactId() {
        UUID contactId = UUID.randomUUID();
        while (contactRepository.existsByContactId(contactId)) {
            contactId = UUID.randomUUID();
        }
        return contactId;
    }


    @Override
    public List<Contact> getContacts(String username, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);
        return contactRepository.findAllByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, language);

    }

    @Override
    public List<Contact> getContact(String username, UUID contactId) {
        User user = userService.findByUsername(username);

        List<Contact>  contacts  =  contactRepository.findAllByUserAndContactIdAndIsDeletedFalseAndIsArchivedFalseOrderByDisplayOrder(user, contactId);
        if (contacts.isEmpty()) {
            throw new ContactNotFoundException("Contact not found with contact  ID: " + contactId);
        }

        return contacts;
    }

    @Override
    public Contact getContact(String username, UUID uuid, String languageCode) {
        User user = userService.findByUsername(username);

        Language language = languageCode == null ?
                portfolioTranslationLanguageService.getPrimaryLanguage(user) :
                portfolioTranslationLanguageService.getLanguageByCode(languageCode);

        return contactRepository.findByUserAndContactIdAndLanguageAndIsDeletedFalseAndIsArchivedFalse(user, uuid, language)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found with contact ID: " + uuid));

    }

    @Override
    public Contact deleteContact(UUID contactId) {
        User user = authenticationService.getAuthenticatedUser();
        Contact contact = contactRepository.findByContactIdAndIsDeletedFalse(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found"));

        if (!Objects.equals(user.getId(), contact.getUser().getId())) {
            throw new ContactNotBelongToUserException("Contact does not belong to user");
        }

        contact.setDeleted(true);
        contact.setUpdatedAt(LocalDateTime.now());

        return contactRepository.save(contact);
    }

    @Override
    public List<ContactRequestVM> parseRequest(String contactRequestJson) throws JsonProcessingException {
       return objectMapper.readValue(
                contactRequestJson,
                new TypeReference<>() {});
    }

}
