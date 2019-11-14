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
        /*HashMap pathVarMap = (HashMap) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long userId = Long.valueOf((String)pathVarMap.get("userId")==null?"":(String)pathVarMap.get("userId"));
        String path = request.getServletPath();



        Long grantId = Long.valueOf((String)pathVarMap.get("grantId")==null?"0":(String)pathVarMap.get("grantId"));
        Long sectionId = Long.valueOf((String)pathVarMap.get("sectionId")==null?"0":(String)pathVarMap.get("sectionId"));
        Long fieldId = Long.valueOf((String)pathVarMap.get("fieldId")==null?"0":(String)pathVarMap.get("fieldId"));
        Long templateId = Long.valueOf((String)pathVarMap.get("templateId")==null?"0":(String)pathVarMap.get("templateId"));
        Long fromState = Long.valueOf((String)pathVarMap.get("fromState")==null?"0":(String)pathVarMap.get("fromState"));
        Long toState = Long.valueOf((String)pathVarMap.get("toState")==null?"0":(String)pathVarMap.get("toState"));

        checkUserIsValid(request,userId); //Global check for user take over*/
        /*switch (_getCategory(path)){
            case "notifications":
                break;
            case "users":
                break;
            case "dashboard":
                break;
            case "grant":
                _grantBelongstoTenant(grantId,tenantCode);
                _userCanModifyGrant(userId,grantId);
                break;
            case "field":
                _grantBelongstoTenant(grantId,tenantCode);
                _userCanModifyGrant(userId,grantId);
                _sectionBelongsToGrant(grantId,sectionId);
                if(fieldId!=0){
                    _fieldBelongsToSection(grantId,sectionId,userId,fieldId);
                }
                break;
            case "section":
                _grantBelongstoTenant(grantId,tenantCode);
                _userCanModifyGrant(userId,grantId);
                if(sectionId!=0) {
                    _sectionBelongsToGrant(grantId, sectionId);
                }
                if(templateId!=0){
                    _templateBelongsToGrant(grantId, templateId);
                }
                break;
            case "assignments":
                _grantBelongstoTenant(grantId,tenantCode);
                //_validUsersAssignedToWorkflow(grantId,userId,request,tenantCode);
                break;
            case "granthistory":
                break;
            case "grantflow":
                _grantBelongstoTenant(grantId,tenantCode);
                _userCanMoveGrantState(grantId,userId, fromState,toState);
                break;
            case "granttemplate":
                break;
            case "templates":
                break;
        }*/

        return super.preHandle(request, response, handler);
    }

    /*private void _validUsersAssignedToWorkflow(Long grantId, Long userId, HttpServletRequest request,String tenantCode) {
        try {
            Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
            User user = userService.getUserById(userId);
            String assignmentsString = IOUtils.toString(new BufferedReader(new InputStreamReader(request.getInputStream())));
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            GrantAssignmentModel assignmentModel = mapper.readValue(assignmentsString, GrantAssignmentModel.class);
            Grant grant = grantService.getById(grantId);

            if(tenantOrg==null){
                throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
            }
            if(assignmentModel.getGrant().getId()!=grantId){
                throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
            }


            List<WorkflowStatus> statuses = workflowStatusService.getTenantWorkflowStatuses("GRANT",tenantOrg.getId());
            for(GrantAssignmentsVO assignmentsVO: assignmentModel.getAssignments()){
                if(assignmentsVO.getAssignments().longValue()!=0 && userService.getUserById(assignmentsVO.getAssignments()).getOrganization().getId().longValue()!=user.getOrganization().getId()){
                 throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
                }
                if(!statuses.stream().filter(st -> st.getId().longValue()!=assignmentsVO.getStateId()).findAny().isPresent()){
                    throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
                }
            }

        } catch (IOException e) {
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }

    }

    private void _userCanMoveGrantState(Long grantId,Long userId, Long fromState, Long toState) {
        if(fromState==0 || toState==0){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }
        Grant grant = grantService.getById(grantId);
        User user = userService.getUserById(userId);
        grant.getGrantStatus().getWorkflow();
        WorkflowStatus from = workflowStatusService.getById(fromState);
        WorkflowStatus to = workflowStatusService.getById(toState);
        WorkflowStatusTransition transition = workflowStatusTransitionService.findByFromAndToStates(from,to);
        if(transition==null){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }
        if(grantService.getGrantAssignmentForGrantStateAndUser(grant,from,user)==null){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }

    }

    private void _templateBelongsToGrant(Long grantId, Long templateId) {
        Grant grant = grantService.getById(grantId);
        if(templateId.longValue()!=grant.getTemplateId()){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }
    }

    private void _fieldBelongsToSection(Long grantId, Long sectionId, Long userId, Long fieldId) {
        Grant grant = grantService.getById(grantId);
        try {
            GrantStringAttribute stringAttribute = grantService.findGrantStringAttributeById(fieldId);
            GrantSpecificSection section = grantService.getGrantSections(grant).stream().filter(sec -> sec.getId().longValue()==sectionId.longValue()).collect(Collectors.toList()).get(0);
            if(!grantService.getAttributesBySection(section).stream().filter(attr -> attr.getId().longValue()==stringAttribute.getSectionAttribute().getId().longValue()).findAny().isPresent()){
                throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_FIELD);
            }
        }catch (NoSuchElementException nse){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_FIELD);
        }


    }

    private void _sectionBelongsToGrant(Long grantId, Long sectionId) {
        Grant grant = grantService.getById(grantId);
        if(!grantService.getGrantSections(grant).stream().filter(sec -> sec.getId().longValue()==sectionId.longValue()).findAny().isPresent()){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_SECTION);
        }

    }

    private void _userCanModifyGrant(Long userId, Long grantId) {
        Grant grant = grantService.getById(grantId);
        if(!grantService.getGrantWorkflowAssignments(grant).stream().filter(ass -> {
            Long assId = ass.getAssignments()==null?0:ass.getAssignments().longValue();
            return assId==userId.longValue() && ass.getStateId()==grant.getGrantStatus().getId();
        }).findAny().isPresent()){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_MODIFY_THIS_GRANT);
        }
    }
*/
    private void checkUserIsValid(HttpServletRequest request,Long userId) {
        SecurityContext context = SecurityContextHolder.getContext();

        User user = userService.getUserById(userId);
        if(!user.getEmailId().equalsIgnoreCase(((String)context.getAuthentication().getPrincipal()).split("\\^")[0])){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }
    }
/*
    private String _getCategory(String path){
        if(path.matches("\\/user\\/[0-9]\\/notifications\\/")){
            return "notifications";
        }
        if(
                path.matches("\\/users\\/")
                || path.matches("\\/users\\/(\\d)+\\/validate-pwd")
                || path.matches("\\/users\\/(\\d)+\\/pwd")
        ){
            return "users";
        }

        if(path.matches("\\/admin\\/workflow\\/grant\\/user\\/(\\d)+") || path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/assignment")){
            return "assignments";
        }


        if(path.matches("\\/users\\/[0-9]\\/dashboard")){
            return "dashboard";
        }
        if(
                path.matches("\\/user\\/[0-9]\\/grant\\/") ||
                path.matches("\\/user\\/(\\d)+\\/grant\\/create\\/(\\d)+") ||
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+")){
            return "grant";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/section\\/(\\d)+\\/field") ||
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/section\\/(\\d)+\\/field\\/(\\d)+") ||
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/field\\/(\\d)+")){
            return "field";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/template\\/(\\d)+\\/section\\/(\\d)+") ||
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/template\\/(\\d)+\\/section\\/(\\w)+")){
            return "section";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/changeHistory")){
            return "granthistory";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/flow\\/(\\d)+\\/(\\d+)")){
            return "grantflow";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/(\\d)+\\/template\\/(\\d)+\\/(\\w)+")){
            return "granttemplate";
        }
        if(
                path.matches("\\/user\\/(\\d)+\\/grant\\/templates")){
            return "templates";
        }

    return "";

    }

    private void _grantBelongstoTenant(Long grantId, String tenantCode){
        Grant grant = grantService.getById(grantId);
        if(!grant.getGrantorOrganization().getCode().equalsIgnoreCase(tenantCode)){
            throw new ResourceNotFoundException(YOU_ARE_NOT_AUTHORIZED_TO_PERFORM_THIS_ACTION);
        }


    }*/
}
