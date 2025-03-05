package org.mbarek0.folioflex.web.controller.translation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mbarek0.folioflex.model.language.Language;
import org.mbarek0.folioflex.service.translation.PortfolioTranslationLanguageService;
import org.mbarek0.folioflex.web.vm.mapper.PortfolioTranslationLanguageMapper;
import org.mbarek0.folioflex.web.vm.request.translation.PortfolioTranslationLanguageRequestVM;
import org.mbarek0.folioflex.web.vm.response.translation.PortfolioTranslationLanguageResponseVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/portfolio/translation/language")
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
            @Valid @RequestBody List<PortfolioTranslationLanguageRequestVM> request) {


        portfolioTranslationLanguageService.save(request);

        return ResponseEntity.ok("Portfolio translation languages created successfully");
    }


    // GET: Retrieve All portfolio translation languages
    @GetMapping
    @Operation(
            summary = "Retrieve all portfolio translation languages",
            description = "Returns a list of all portfolio translation languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolio translation languages"),
                    @ApiResponse(responseCode = "404", description = "Portfolio translation languages not found")
            }
    )
    public ResponseEntity<List<Language>> getPortfolioTranslationLanguageById( ) {

        List<Language> languages = portfolioTranslationLanguageService.getAllPortfolioTranslationLanguage();
        return ResponseEntity.ok(languages);

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
    public ResponseEntity<List<PortfolioTranslationLanguageResponseVM>> getAllPortfolioTranslationLanguagesForUser(
            @Parameter(
                    name = "userId",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @PathVariable Long userId) {

        List<PortfolioTranslationLanguageResponseVM> portfolioTranslationLanguages = portfolioTranslationLanguageService.getAllPortfolioTranslationLanguagesForUser(userId).stream()
                .map(portfolioTranslationLanguageMapper::toVM)
                .toList();

        return ResponseEntity.ok(portfolioTranslationLanguages);

    }

    // PUT: Add a single portfolio translation language
    @PostMapping("/add")
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
                    name = "userId",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @RequestParam Long userId,

            @Parameter(
                    name = "languageId",
                    description = "Language ID",
                    required = true,
                    example = "1"
            )
            @RequestParam Long languageId) {

        portfolioTranslationLanguageService.save(userId, languageId);
        return ResponseEntity.ok("Portfolio translation language added successfully");
    }


    // PUT: update isPrimary field of a portfolio translation language
    @PutMapping("/primary")
    @Operation(
            summary = "Update isPrimary field of a portfolio translation language",
            description = "Updates the isPrimary field of a portfolio translation language",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Portfolio translation language updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Portfolio translation language not found")
            }
    )
    public ResponseEntity<String> updatePortfolioTranslationLanguagePrimary(
            @Parameter(
                    name = "userId",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @RequestParam Long userId,

            @Parameter(
                    name = "languageId",
                    description = "Language ID",
                    required = true,
                    example = "1"
            )
            @RequestParam Long languageId) {

        portfolioTranslationLanguageService.updatePortfolioTranslationLanguagePrimary(userId, languageId);
        return ResponseEntity.ok("Portfolio translation language updated successfully");
    }

    // PUT: Delete a portfolio translation language
    @DeleteMapping
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
                    name = "userId",
                    description = "User ID",
                    required = true,
                    example = "1234"
            )
            @RequestParam Long userId,

            @Parameter(
                    name = "languageId",
                    description = "Language ID",
                    required = true,
                    example = "1"
            )
            @RequestParam Long languageId) {

        portfolioTranslationLanguageService.deletePortfolioTranslationLanguage(userId, languageId);
        return ResponseEntity.ok("Portfolio translation language deleted successfully");
    }


}