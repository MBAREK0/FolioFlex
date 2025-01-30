package org.mbarek0.folioflex.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.PortfolioTranslationLanguage;
import org.mbarek0.folioflex.service.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.web.vm.mapper.PortfolioTranslationLanguageMapper;
import org.mbarek0.folioflex.web.vm.request.CreatePortfolioTranslationLanguageVM;
import org.mbarek0.folioflex.web.vm.response.PortfolioTranslationLanguageVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/portfolio/translation/language")
@Tag(name = "Portfolio Translation Language", description = "APIs for managing portfolio translation languages")
public class PortfolioTranslationLanguageController {

    private final PortfolioTranslationLanguageService portfolioTranslationLanguageService;
    private final PortfolioTranslationLanguageMapper  portfolioTranslationLanguageMapper;



    // POST: Create a new collection of portfolio translation languages
    @PostMapping
    @Operation(
            summary = "Create a new collection of portfolio translation languages",
            description = "Creates a new portfolio translation languages for a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Portfolio translation languages created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<String> createPortfolioTranslationLanguage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of portfolio translation languages to create",
                    required = true
            )
            @Valid @RequestBody List<CreatePortfolioTranslationLanguageVM> request) {


        portfolioTranslationLanguageService.save(request);

        return ResponseEntity.ok("Portfolio translation languages created successfully");
    }


    // GET: Retrieve all portfolio translation languages for a user
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Retrieve all portfolio translation languages for a user",
            description = "Returns a list of all portfolio translation languages for the specified user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolio translation languages"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<PortfolioTranslationLanguageVM>> getAllPortfolioTranslationLanguagesForUser(
            @Parameter(
                    name = "userId",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @PathVariable Long userId) {

        List<PortfolioTranslationLanguageVM> portfolioTranslationLanguages = portfolioTranslationLanguageService.getAllPortfolioTranslationLanguagesForUser(userId).stream()
                .map(portfolioTranslationLanguageMapper::toVM)
                .toList();

        return ResponseEntity.ok(portfolioTranslationLanguages);

    }

    // GET: Retrieve a specific portfolio translation language by ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a specific portfolio translation language by ID",
            description = "Returns the details of a specific portfolio translation language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolio translation language"),
                    @ApiResponse(responseCode = "404", description = "Portfolio translation language not found")
            }
    )
    public ResponseEntity<String> getPortfolioTranslationLanguageById(
            @Parameter(
                    name = "id",
                    description = "Portfolio translation language ID",
                    required = true,
                    example = "1"
            )
            @PathVariable String id) {

        // Logic to retrieve a specific portfolio translation language by ID
        return ResponseEntity.ok("Retrieved portfolio translation language with ID: " + id);
    }

    // PUT: Delete a portfolio translation language
    @PutMapping("/delete")
    @Operation(
            summary = "Delete a portfolio translation language",
            description = "Deletes a specific portfolio translation language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Portfolio translation language deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Portfolio translation language not found")
            }
    )
    public ResponseEntity<String> deletePortfolioTranslationLanguage(
            @Parameter(
                    name = "id",
                    description = "Portfolio translation language ID",
                    required = true,
                    example = "1"
            )
            @RequestParam String id) {

        // Logic to delete a portfolio translation language
        return ResponseEntity.ok("Deleted portfolio translation language with ID: " + id);
    }

    // PUT: Add a single portfolio translation language
    @PutMapping("/add")
    @Operation(
            summary = "Add a single portfolio translation language",
            description = "Adds a new portfolio translation language for a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Portfolio translation language added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<String> addPortfolioTranslationLanguage(
            @Parameter(
                    name = "user",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @RequestParam String user,

            @Parameter(
                    name = "language",
                    description = "Language ID",
                    required = true,
                    example = "1"
            )
            @RequestParam String language) {

        // Logic to add a single portfolio translation language
        return ResponseEntity.ok("Added portfolio translation language for user: " + user + ", language: " + language);
    }
}