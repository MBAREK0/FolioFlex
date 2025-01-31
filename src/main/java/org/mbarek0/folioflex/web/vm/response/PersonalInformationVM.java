package org.mbarek0.folioflex.web.vm.response;

import java.time.LocalDateTime;

public record PersonalInformationVM(
        Long id,
        Long userId,
        String languageCode,
        String profilePhoto,
        String backgroundBanner,
        String firstName,
        String lastName,
        String headline,
        String location,
        String about,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}