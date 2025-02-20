package org.mbarek0.folioflex.service.portfolio_components;


import org.mbarek0.folioflex.model.portfolio_components.Certification;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.CertificationRequestVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CertificationService {

    List<Certification> createCertification(List<CertificationRequestVM> request, MultipartFile certificationImageFile);

    List<Certification> getAllCertifications(String username, String languageCode);

    List<Certification> getAllCertifications(String username, UUID certificationId);

    Certification getCertification(String username, UUID uuid, String languageCode);

    List<Certification> updateCertification(UUID uuid, List<CertificationRequestVM> certificationVM, MultipartFile certificationImageFile);

    List<Certification> reorder(List<ReorderRequest> reorderRequests);

    List<Certification> deleteCertification(UUID uuid);

    List<Certification> archiveCertification(UUID uuid);
}