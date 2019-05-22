package org.codealpha.gmsservice.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureBefore
class GranteeServiceTest {

  @Autowired
  private GranteeService granteeService;

  @MockBean
  private GrantRepository grantRepository;

  private Long granteeOrgId;
  private Organization tenantOrg;
  private Organization platformOrg;
  private Long userRoleId;
  List<Grant> grantorGrants = new ArrayList<>();
  List<Grant> platformGrants = new ArrayList<>();

  @BeforeEach
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

    userRoleId = 1L;


  }

  @Test
  void whenValidUserAndTenantEqualsGrantor_ThenReturnOneGrant() {
    Mockito.doReturn(grantorGrants).when(grantRepository)
        .findGrantsOfGranteeForTenantOrg(granteeOrgId, tenantOrg.getId(), userRoleId);
    List<Grant> grants = granteeService.getGrantsOfGranteeForGrantor(granteeOrgId, tenantOrg,
        userRoleId);
    Assert.assertEquals(1,grants.size());
  }

  @Test
  void whenValidUserAndTenantEqualsPlatform_ThenReturnOneGrant() {
    Mockito.doReturn(platformGrants).when(grantRepository)
        .findAllGrantsOfGrantee(granteeOrgId);
    List<Grant> grants = granteeService.getGrantsOfGranteeForGrantor(granteeOrgId, platformOrg,
        userRoleId);
    Assert.assertEquals(2,grants.size());
  }
}
