package org.mbarek0.folioflex.service.portfolio_components;


import org.mbarek0.folioflex.model.portfolio_components.Skill;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.SkillRequestVM;
import org.mbarek0.folioflex.web.vm.request.portfolio_components.ReorderRequest;

import java.util.List;
import java.util.UUID;

public interface SkillService {

    List<Skill> createSkill(List<SkillRequestVM> request);

    List<Skill> getAllSkills(String username, String languageCode);

    List<Skill> getAllSkills(String username, UUID skillId);

    Skill getSkill(String username, UUID uuid, String languageCode);

    List<Skill> updateSkill(UUID uuid, List<SkillRequestVM> skillVM);

    List<Skill> reorder(List<ReorderRequest> reorderRequests);

    List<Skill> deleteSkill(UUID uuid);

    List<Skill> archiveSkill(UUID uuid);
}