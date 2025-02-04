package org.mbarek0.folioflex.repository;

import org.mbarek0.folioflex.model.Language;
import org.mbarek0.folioflex.model.User;
import org.mbarek0.folioflex.model.portfolio_components.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, Long> {

    Optional<PersonalInformation> findByUserAndLanguageAndIsDeletedFalseAndIsArchivedFalse(User user, Language language);

    @Query("SELECT COUNT(p) > 0 FROM PersonalInformation p WHERE " +
            "(p.profilePhoto = :url OR p.backgroundBanner = :url) AND p.user = :user AND p.isDeleted = false AND p.isArchived = false")
    boolean existsByUserAndProfilePhotoUrlOrBackgroundBannerUrl(
            @Param("user") User user,
            @Param("url") String url
    );

    @Query("SELECT pi.language FROM PersonalInformation pi WHERE pi.user.id = :userId AND pi.isDeleted = false AND pi.isArchived = false")
    List<Language> findLanguagesByUserId(@Param("userId") Long userId);

    List<PersonalInformation> findAllByUserAndIsDeletedFalseAndIsArchivedFalse(User user);
}
