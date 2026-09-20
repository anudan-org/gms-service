package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.services.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor implements HandlerInterceptor {
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION = "You are not authorized to perform this action";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_FIELD = "You are not authorized to modify this field";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_SECTION = "You are not authorized to modify this section.";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_GRANT = "You are not authorized to modify this grant.";
    private final UserService userService;
    private final WorkflowPermissionService workflowPermissionService;
    private final AppConfigService appConfigService;
    private final WorkflowStatusService workflowStatusService;
    private final WorkflowStatusTransitionService workflowStatusTransitionService;
    private final OrganizationService organizationService;

    public ApiInterceptor(
            UserService userService,
            WorkflowPermissionService workflowPermissionService,
            AppConfigService appConfigService,
            WorkflowStatusService workflowStatusService,
            WorkflowStatusTransitionService workflowStatusTransitionService,
            OrganizationService organizationService
    ) {
        this.userService = userService;
        this.workflowPermissionService = workflowPermissionService;
        this.appConfigService = appConfigService;
        this.workflowStatusService = workflowStatusService;
        this.workflowStatusTransitionService = workflowStatusTransitionService;
        this.organizationService = organizationService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        // Legacy behaviour: allow request to proceed
        return true;
    }
}
