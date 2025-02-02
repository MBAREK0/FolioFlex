package org.mbarek0.folioflex.web.vm.response.portfolio_components;

import java.time.LocalDateTime;

public record PersonalInformationResponseVM(
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