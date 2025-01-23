package org.mbarek0.folioflex.model.enums;

public enum Permission {
    // Portfolio Owner Permissions
    PORTFOLIO_OWNER_CREATE_PORTFOLIO("portfolio_owner:create_portfolio"),
    PORTFOLIO_OWNER_VIEW_PORTFOLIO("portfolio_owner:view_portfolio"),
    PORTFOLIO_OWNER_EDIT_PORTFOLIO("portfolio_owner:edit_portfolio"),
    PORTFOLIO_OWNER_DELETE_PORTFOLIO("portfolio_owner:delete_portfolio"),
    PORTFOLIO_OWNER_PREVIEW_PORTFOLIO("portfolio_owner:preview_portfolio"),
    PORTFOLIO_OWNER_MANAGE_PERSONAL_INFO("portfolio_owner:manage_personal_info"),
    PORTFOLIO_OWNER_MANAGE_PROJECTS("portfolio_owner:manage_projects"),
    PORTFOLIO_OWNER_MANAGE_EXPERIENCES("portfolio_owner:manage_experiences"),
    PORTFOLIO_OWNER_MANAGE_SKILLS("portfolio_owner:manage_skills"),
    PORTFOLIO_OWNER_MANAGE_AWARDS("portfolio_owner:manage_awards"),
    PORTFOLIO_OWNER_UPLOAD_CV("portfolio_owner:upload_cv"),
    PORTFOLIO_OWNER_MANAGE_CONTACTS("portfolio_owner:manage_contacts"),
    PORTFOLIO_OWNER_MANAGE_CERTIFICATIONS("portfolio_owner:manage_certifications"),
    PORTFOLIO_OWNER_MANAGE_ACCOUNT_SETTINGS("portfolio_owner:manage_account_settings"),
    PORTFOLIO_OWNER_VIEW_OVERVIEW("portfolio_owner:view_overview"),
    PORTFOLIO_OWNER_CHOOSE_TEMPLATE("portfolio_owner:choose_template"),
    PORTFOLIO_OWNER_REQUEST_TEMPLATE_CUSTOMIZATION("portfolio_owner:request_template_customization"),
    PORTFOLIO_OWNER_CHANGE_TEMPLATE("portfolio_owner:change_template"),
    PORTFOLIO_OWNER_MANAGE_LANGUAGES("portfolio_owner:manage_languages"),
    PORTFOLIO_OWNER_LINK_SUBDOMAIN("portfolio_owner:link_subdomain"),
    PORTFOLIO_OWNER_CONNECT_CUSTOM_DOMAIN("portfolio_owner:connect_custom_domain"),

    // Admin Permissions
    ADMIN_VIEW_USERS("admin:view_users"),
    ADMIN_MANAGE_USERS("admin:manage_users"),
    ADMIN_VIEW_STATISTICS("admin:view_statistics"),
    ADMIN_ADD_TEMPLATE("admin:add_template"),
    ADMIN_EDIT_TEMPLATE("admin:edit_template"),
    ADMIN_DELETE_TEMPLATE("admin:delete_template"),
    ADMIN_SET_TEMPLATE_PRICING("admin:set_template_pricing");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
