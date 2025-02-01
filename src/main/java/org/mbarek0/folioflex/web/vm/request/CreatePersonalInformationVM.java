package org.mbarek0.folioflex.web.vm.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonalInformationVM {
    @NotNull(message = "User ID cannot be null")
    Long userId;

    @NotBlank(message = "Language code is required")
    @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
    String languageCode;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    String lastName;

    MultipartFile profilePhotoFile;
    String profilePhotoUrl;

    MultipartFile backgroundBannerFile;
    String backgroundBannerUrl;

    @NotBlank(message = "Headline is required")
    @Size(max = 100, message = "Headline cannot exceed 100 characters")
    String headline;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    String location;

    @Size(max = 2000, message = "About cannot exceed 2000 characters")
    String about;


}

