package org.mbarek0.folioflex.web.controller.portfolio_components;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.portfolio_components.Contact;
import org.mbarek0.folioflex.service.portfolio_components.ContactService;
import org.mbarek0.folioflex.web.vm.mapper.ContactMapper;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ContactRequestVM;
import org.mbarek0.folioflex.web.vm.response.portfolio_components.ContactResponseVM;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/contacts")
@Tag(name = "Contacts", description = "APIs for managing user contacts")
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create a new contact",
            description = "Creates a new contact with an optional icon file upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contact created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or validation errors")
            }
    )
    public ResponseEntity<List<ContactResponseVM>> createContact(
            @Valid @RequestParam("contacts") String contacts,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {

        List<ContactRequestVM> contactRequest = contactService.parseRequest(contacts);
        List<Contact> createdContact = contactService.createContact(contactRequest, iconFile);

        return ResponseEntity.ok(
                createdContact.stream()
                        .map(contactMapper::toVM)
                        .toList()
        );
    }



//    @PutMapping(value = "/{contactId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(
//            summary = "Update an existing contact",
//            description = "Updates a contact with an optional new icon file upload",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
//                    @ApiResponse(responseCode = "400", description = "Invalid input data or validation errors"),
//                    @ApiResponse(responseCode = "404", description = "Contact not found")
//            }
//    )
//    public ResponseEntity<ContactResponseVM> updateContact(
//            @PathVariable UUID contactId,
//            @RequestParam("contact") String contactRequestJson,
//            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) throws IOException {
//
//        List<ContactRequestVM> contactRequest = contactService.parseRequest(contactRequestJson);
//        ContactResponseVM updatedContact = contactService.updateContact(contactId, contactRequest, iconFile);
//
//        return ResponseEntity.ok(updatedContact);
//    }
//
//    @GetMapping("/user/{userId}")
//    @Operation(
//            summary = "Get all contacts for a user",
//            description = "Retrieves all contacts belonging to a specific user",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully"),
//                    @ApiResponse(responseCode = "404", description = "User not found")
//            }
//    )
//    public ResponseEntity<List<ContactResponseVM>> getContactsByUser(@PathVariable Long userId) {
//        List<ContactResponseVM> contacts = contactService.getContactsByUser(userId)
//                .stream()
//                .map(contactMapper::toVM)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(contacts);
//    }

    @GetMapping("/user/{username}/primary")
    @Operation(
            summary = "Get primary contact for a user",
            description = "Retrieves the primary contact for a specific user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Primary contact retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<ContactResponseVM>> getPrimaryContactByUser(@PathVariable String username) {
        List<Contact> contact = contactService.getContacts(username,(String) null);
        return ResponseEntity.ok(
                contact.stream()
                        .map(contactMapper::toVM)
                        .toList()
        );
    }

    @GetMapping("/user/{username}/language/{languageCode}")
    @Operation(
            summary = "Get contacts for a user in a specific language",
            description = "Retrieves all contacts for a specific user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<ContactResponseVM>> getContactsByUserAndLanguage(
            @PathVariable String username,
            @PathVariable String languageCode) {
        List<Contact> contact = contactService.getContacts(username, languageCode);
        return ResponseEntity.ok(
                contact.stream()
                        .map(contactMapper::toVM)
                        .toList()
        );
    }

    @GetMapping("/user/{username}/contact/{uuid}")
    @Operation(
            summary = "Get a contact by its ID",
            description = "Retrieves a contact by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contact retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Contact not found")
            }
    )
    public ResponseEntity<List<ContactResponseVM>> getContactById(
            @PathVariable String username,
            @PathVariable UUID uuid) {
        List<Contact> contact = contactService.getContact(username, uuid);
        return ResponseEntity.ok(
                contact.stream()
                        .map(contactMapper::toVM)
                        .toList()
        );
    }

    @GetMapping("/user/{username}/contact/{uuid}/language/primary")
    @Operation(
            summary = "Get primary contact for a user in a specific language",
            description = "Retrieves the primary contact for a specific user in a specific language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Primary contact retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<ContactResponseVM> getPrimaryContactByUserAndLanguage(
            @PathVariable String username,
            @PathVariable UUID uuid) {
        Contact contact = contactService.getContact(username, uuid,(String) null);
        return ResponseEntity.ok(contactMapper.toVM(contact));
    }

    @GetMapping("/user/{username}/contact/{uuid}/language/{languageCode}")
    @Operation(
            summary = "Get a contact by its ID and language",
            description = "Retrieves a contact by its ID and language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contact retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Contact not found")
            }
    )
    public ResponseEntity<ContactResponseVM> getContactByIdAndLanguage(
            @PathVariable String username,
            @PathVariable UUID uuid,
            @PathVariable String languageCode) {
        Contact contact = contactService.getContact(username, uuid, languageCode);
        return ResponseEntity.ok(contactMapper.toVM(contact));
    }

    @DeleteMapping("/{contactId}")
    @Operation(
            summary = "Delete a contact",
            description = "Deletes a contact by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Contact deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Contact not found")
            }
    )
    public ResponseEntity<Contact> deleteContact(@PathVariable UUID contactId) {
        Contact contact = contactService.deleteContact(contactId);
        return ResponseEntity.ok(contact);
    }
}
