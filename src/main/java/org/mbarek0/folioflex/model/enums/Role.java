package org.mbarek0.folioflex.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.mbarek0.folioflex.model.enums.Permission.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    USER(
            Set.of(
                    PORTFOLIO_OWNER_PREVIEW_PORTFOLIO,
                    PORTFOLIO_OWNER_MANAGE_PERSONAL_INFO,
                    PORTFOLIO_OWNER_MANAGE_PROJECTS,
                    PORTFOLIO_OWNER_MANAGE_EXPERIENCES,
                    PORTFOLIO_OWNER_MANAGE_SKILLS,
                    PORTFOLIO_OWNER_MANAGE_AWARDS,
                    PORTFOLIO_OWNER_UPLOAD_CV,
                    PORTFOLIO_OWNER_MANAGE_CONTACTS,
                    PORTFOLIO_OWNER_MANAGE_CERTIFICATIONS,
                    PORTFOLIO_OWNER_MANAGE_ACCOUNT_SETTINGS,
                    PORTFOLIO_OWNER_VIEW_OVERVIEW,
                    PORTFOLIO_OWNER_CHOOSE_TEMPLATE,
                    PORTFOLIO_OWNER_REQUEST_TEMPLATE_CUSTOMIZATION,
                    PORTFOLIO_OWNER_CHANGE_TEMPLATE,
                    PORTFOLIO_OWNER_MANAGE_LANGUAGES,
                    PORTFOLIO_OWNER_LINK_SUBDOMAIN,
                    PORTFOLIO_OWNER_CONNECT_CUSTOM_DOMAIN
            )
    ),

    ADMIN(
            Set.of(
                    ADMIN_VIEW_USERS,
                    ADMIN_MANAGE_USERS,
                    ADMIN_VIEW_STATISTICS,
                    ADMIN_ADD_TEMPLATE,
                    ADMIN_EDIT_TEMPLATE,
                    ADMIN_DELETE_TEMPLATE,
                    ADMIN_SET_TEMPLATE_PRICING,
                    ADMIN_CREATE_PORTFOLIO,
                    ADMIN_VIEW_PORTFOLIO,
                    ADMIN_EDIT_PORTFOLIO,
                    ADMIN_DELETE_PORTFOLIO
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities =getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }
}