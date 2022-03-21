package org.codealpha.gmsservice.security;

import org.codealpha.gmsservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION = "You are not authorized to perform this action";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_FIELD = "You are not authorized to modify this field";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_SECTION = "You are not authorized to modify this section.";
    public static final String YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_GRANT = "You are not authorized to modify this grant.";
    @Autowired
    private UserService userService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private WorkflowStatusTransitionService workflowStatusTransitionService;
    @Autowired
    private OrganizationService organizationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }
}
