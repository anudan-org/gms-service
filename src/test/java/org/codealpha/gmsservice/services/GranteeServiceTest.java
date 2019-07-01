package org.codealpha.gmsservice.services;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Role;
import org.codealpha.gmsservice.entities.RolesPermission;
import org.codealpha.gmsservice.entities.UserRole;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GranteeServiceTest {

  @Autowired
  private GranteeService granteeService;

  @MockBean
  private GrantRepository grantRepository;

  private Long granteeOrgId;
  private Organization tenantOrg;
  private Organization platformOrg;
  private List<UserRole> userRoles;
  List<Grant> grantorGrants = new ArrayList<>();
  List<Grant> platformGrants = new ArrayList<>();

  @Before
  public void setup() {

    grantorGrants.add(new Grant());

    platformGrants.add(new Grant());
    platformGrants.add(new Grant());

    granteeOrgId = 1L;

    tenantOrg = new Granter();
    tenantOrg.setOrganizationType("GRANTER");
    tenantOrg.setId(1L);

    platformOrg = new Granter();
    platformOrg.setOrganizationType("PLATFORM");
    platformOrg.setId(1L);

    userRoles = new ArrayList<>();
    UserRole userRole = new UserRole();
    Role role = new Role();
    role.setId(1l);
    role.setName("Program Manager");
    List<RolesPermission> permissions = new ArrayList<>();
    RolesPermission permission = new RolesPermission();
    permission.setId(1l);
    permission.setPermission("Create Grant");
    permissions.add(permission);
    role.setPermissions(permissions);
    userRole.setRole(role);
    userRoles.add(userRole);
  }

  @Test
  public void whenValidUserAndTenantEqualsGrantor_ThenReturnOneGrant() {
    List<Long> userRoleIds = userRoles.stream().map(e->new Long(e.getRole().getId())).collect(
        Collectors.toList());
    Mockito.doReturn(grantorGrants).when(grantRepository)
        .findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(), userRoleIds);
    List<UserRole> userRoles = new ArrayList<>();
    List<Grant> grants = granteeService.getGrantsOfGranteeForGrantor(granteeOrgId, tenantOrg,
        userRoles);
    Assert.assertEquals(0, grants.size());
  }

  @Test
  public void whenValidUserAndTenantEqualsPlatform_ThenReturnOneGrant() {
    Mockito.doReturn(platformGrants).when(grantRepository)
        .findAllGrantsOfGrantee(granteeOrgId);
    List<Grant> grants = granteeService.getGrantsOfGranteeForGrantor(granteeOrgId, platformOrg,
        userRoles);
    Assert.assertEquals(2, grants.size());
  }
}
