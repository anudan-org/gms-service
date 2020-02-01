package org.codealpha.gmsservice.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.models.GrantAssignmentModel;
import org.codealpha.gmsservice.models.GrantAssignmentsVO;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        System.out.println(request.getServletPath());
        return super.preHandle(request, response, handler);
    }
}
