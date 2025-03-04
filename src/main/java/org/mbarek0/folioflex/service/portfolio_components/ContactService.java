package org.mbarek0.folioflex.service.portfolio_components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.Contact;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ContactRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.ContactResponseVM;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public interface ContactService {

    List<ContactRequestVM> parseRequest(String contactRequestJson) throws JsonProcessingException;

    List<Contact> createContact(List<ContactRequestVM> contactRequest, MultipartFile iconFile);

    Contact deleteContact(UUID contactId);

    List<Contact> getContacts(String username, String s);

    List<Contact> getContact(String username, UUID uuid);

    Contact getContact(String username, UUID uuid, String languageCode);
}
