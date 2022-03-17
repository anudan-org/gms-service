package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.models.UIConfig;

/**
 * @author Developer code-alpha.org
 **/
public interface GranterConfigurationService {

	UIConfig getUiConfiguration(Long granterId);

}
