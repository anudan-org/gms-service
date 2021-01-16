package org.codealpha.gmsservice.services;

import java.util.Optional;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.models.UIConfig;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Developer code-alpha.org
 **/
@Service
public class GranterConfigurationServiceImpl implements GranterConfigurationService {

	@Autowired
	private GranterRepository repository;

	@Override
	public UIConfig getUiConfiguration(Long granterId) {

		UIConfig config = new UIConfig();
		Optional<Granter> granter = repository.findById(granterId);

		if (granter.isPresent()){
			config.setLogoUrl(granter.get().getImageName());
			config.setNavbarColor(granter.get().getNavbarColor());
			config.setNavbarTextColor(granter.get().getNavbarTextColor());
		}
		return config;
	}
}
