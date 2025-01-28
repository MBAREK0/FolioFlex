package org.mbarek0.folioflex.web.controller.ai;

import org.mbarek0.folioflex.service.ai.GeminiTranslationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/translate")
@Tag(name = "Translation API", description = "API for text translation using Gemini AI")
public class TranslationController {

    private final GeminiTranslationService translationService;

    public TranslationController(GeminiTranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping
    @Operation(
            summary = "Translate text to target language",
            description = "Translates input text to the specified target language using Gemini AI"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful translation"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or translation failure")
    })
    public String translateText(
            @Parameter(
                    name = "text",
                    description = "Text to be translated",
                    required = true,
                    example = "Hello, world!"
            )
            @RequestParam String text,

            @Parameter(
                    name = "targetLanguage",
                    description = "Target language code (e.g., 'es' for Spanish, 'fr' for French)",
                    required = true,
                    example = "es"
            )
            @RequestParam String targetLanguage) {
        return translationService.translateText(text, targetLanguage);
    }
}