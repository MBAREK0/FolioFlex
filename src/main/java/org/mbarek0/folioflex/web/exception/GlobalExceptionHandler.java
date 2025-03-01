package org.mbarek0.folioflex.web.exception;


import io.jsonwebtoken.ExpiredJwtException;
import org.mbarek0.folioflex.web.exception.authenticationExs.AuthenticatedUserNotFoundInDatabaseException;
import org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs.CertificationAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs.CertificationNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs.CertificationNotFoundException;
import org.mbarek0.folioflex.web.exception.portfolioExs.certificationExs.InvalidCertificationDataException;
import org.mbarek0.folioflex.web.exception.portfolioExs.educationExs.EducationNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.portfolioExs.educationExs.EducationNotFoundException;
import org.mbarek0.folioflex.web.exception.portfolioExs.educationExs.InvalidEducationDataException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.InvalidImageUrlException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationNotBelongToUser;
import org.mbarek0.folioflex.web.exception.portfolioExs.personal_informationExs.PersonalInformationNotFoundException;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.InvalidWorkExperienceDataException;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.WorkExperienceAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.WorkExperienceNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.portfolioExs.work_experienceExs.WorkExperienceNotFoundException;
import org.mbarek0.folioflex.web.exception.skillExs.InvalidSkillDataException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillNotBelongToUserException;
import org.mbarek0.folioflex.web.exception.skillExs.SkillNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.EnglishLanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.LanguageNotFoundException;
import org.mbarek0.folioflex.web.exception.translationExs.PortfolioTranslationLanguageAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.translationExs.UserDontHaveLanguageException;
import org.mbarek0.folioflex.web.exception.userExs.UserIdDoesNotMatchTheAuthenticatedUserException;
import org.mbarek0.folioflex.web.exception.userExs.UserNameAlreadyExistsException;
import org.mbarek0.folioflex.web.exception.userExs.UserNotFoundException;
import org.mbarek0.folioflex.web.exception.userExs.UsernameOrPasswordInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.management.relation.RoleNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /** --------------------------------------- User Exceptions  */
    //  ---------------  UserNameAlreadyExistsException
    @ExceptionHandler(UserNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleUserNameAlreadyExistsException(
            UserNameAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    //  ---------------  UsernameOrPasswordInvalidException
    @ExceptionHandler(UsernameOrPasswordInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, Object>> handleUsernameOrPasswordInvalidException(
            UsernameOrPasswordInvalidException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    //  ---------------  RoleNotFoundException
    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleRoleNotFoundException(
            RoleNotFoundException ex, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", Instant.now().toString());
        responseBody.put("status", HttpStatus.NOT_FOUND.value());
        responseBody.put("error", "Not Found");
        responseBody.put("message", ex.getMessage());
        responseBody.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    //  ---------------  UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // ---------------  UserIdDoesNotMatchTheAuthenticatedUserException
    @ExceptionHandler(UserIdDoesNotMatchTheAuthenticatedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, Object>> handleUserIdDoesNotMatchTheAuthenticatedUserException(
            UserIdDoesNotMatchTheAuthenticatedUserException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    /** --------------------------------------- Authentication Exceptions  */

    //ExpiredJwtException
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(
            ExpiredJwtException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    // AuthenticatedUserNotFoundInDatabaseException
    @ExceptionHandler(AuthenticatedUserNotFoundInDatabaseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleAuthenticatedUserNotFoundInDatabaseException(
            AuthenticatedUserNotFoundInDatabaseException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    /** --------------------------------------- Translation Exceptions  */

    // Handle LanguageNotFoundException
    @ExceptionHandler(LanguageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleLanguageNotFoundException(
            LanguageNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // Handle PortfolioTranslationLanguageAlreadyExistsException
    @ExceptionHandler(PortfolioTranslationLanguageAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handlePortfolioTranslationLanguageAlreadyExistsException(
            PortfolioTranslationLanguageAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Handle EnglishLanguageNotFoundException
    @ExceptionHandler(EnglishLanguageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleEnglishLanguageNotFoundException(
            EnglishLanguageNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // hndle UserDontHaveLanguageException
    @ExceptionHandler(UserDontHaveLanguageException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleUserDontHaveLanguageException(
            UserDontHaveLanguageException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // ---------------------------------------- Portfolio Exceptions

    // Handle PersonalInformationAlreadyExistsException
    @ExceptionHandler(PersonalInformationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handlePersonalInformationAlreadyExistsException(
            PersonalInformationAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Handle InvalidImageUrlException
    @ExceptionHandler(InvalidImageUrlException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleInvalidImageUrlException(
            InvalidImageUrlException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // Handle PersonalInformationNotFoundException
    @ExceptionHandler(PersonalInformationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handlePersonalInformationNotFoundException(
            PersonalInformationNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // Handle PersonalInformationNotBelongToUser
    @ExceptionHandler(PersonalInformationNotBelongToUser.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handlePersonalInformationNotBelongToUser(
            PersonalInformationNotBelongToUser ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }


    // Handle InvalidWorkExperienceDataException
    @ExceptionHandler(InvalidWorkExperienceDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidWorkExperienceDataException(
            InvalidWorkExperienceDataException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // Handle WorkExperienceNotFoundException
    @ExceptionHandler(WorkExperienceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleWorkExperienceNotFoundException(
            WorkExperienceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // Handle WorkExperienceAlreadyExistsException
    @ExceptionHandler(WorkExperienceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleWorkExperienceAlreadyExistsException(
            WorkExperienceAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // handle WorkExperienceNotBelongToUserException
    @ExceptionHandler(WorkExperienceNotBelongToUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleWorkExperienceNotBelongToUserException(
            WorkExperienceNotBelongToUserException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle InvalidEducationDataException
    @ExceptionHandler(InvalidEducationDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidEducationDataException(
            InvalidEducationDataException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // handle EducationNotFoundException
    @ExceptionHandler(EducationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleEducationNotFoundException(
            EducationNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle EducationNotBelongToUserException
    @ExceptionHandler(EducationNotBelongToUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleEducationNotBelongToUserException(
            EducationNotBelongToUserException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle CertificationAlreadyExistsException
    @ExceptionHandler(CertificationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleCertificationAlreadyExistsException(
            CertificationAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // handle CertificationNotFoundException
    @ExceptionHandler(CertificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleCertificationNotFoundException(
            CertificationNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle InvalidCertificationDataException
    @ExceptionHandler(InvalidCertificationDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidCertificationDataException(
            InvalidCertificationDataException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // handle CertificationNotBelongToUserException
    @ExceptionHandler(CertificationNotBelongToUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleCertificationNotBelongToUserException(
            CertificationNotBelongToUserException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle InvalidSkillDataException
    @ExceptionHandler(InvalidSkillDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidSkillDataException(
            InvalidSkillDataException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // handle SkillAlreadyExistsException
    @ExceptionHandler(SkillAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, Object>> handleSkillAlreadyExistsException(
            SkillAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // handle SkillNotBelongToUserException
    @ExceptionHandler(SkillNotBelongToUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleSkillNotBelongToUserException(
            SkillNotBelongToUserException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    // handle SkillNotFoundException
    @ExceptionHandler(SkillNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleSkillNotFoundException(
            SkillNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    /** --------------------------------------- Global Exceptions  */

    //  ---------------  FieldCannotBeNullException
    @ExceptionHandler(FieldCannotBeNullException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleFieldCannotBeNullException(
            FieldCannotBeNullException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // Handle InvalidInputException
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleInvalidInputException(
            InvalidInputException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }



    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Collect detailed validation errors
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("field", fieldError.getField());
                    errorDetails.put("message", fieldError.getDefaultMessage());
                    return errorDetails;
                })
                .collect(Collectors.toList());

        // Create a generic message for the base response
        String baseMessage = "Validation failed";
        RuntimeException validationEx = new RuntimeException(baseMessage);

        // Build the base error response using the helper method
        ResponseEntity<Map<String, Object>> responseEntity =
                buildErrorResponse(validationEx, HttpStatus.BAD_REQUEST, request);

        // Enhance the response with validation details
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody != null) {
            responseBody.put("message", baseMessage + " (" + errors.size() + " error(s))");
            responseBody.put("errors", errors);  // Include detailed errors
        }

        return responseEntity;
    }


    // Helper method to build the error response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            RuntimeException ex, HttpStatus status, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", Instant.now().toString());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", ex.getMessage());
        responseBody.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(status).body(responseBody);
    }
}
