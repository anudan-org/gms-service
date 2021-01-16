package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.models.UIConfig;
import org.springframework.stereotype.Service;

/**
 * @author Developer code-alpha.org
 **/
public interface GranterConfigurationService {

	UIConfig getUiConfiguration(Long granterId);

}
