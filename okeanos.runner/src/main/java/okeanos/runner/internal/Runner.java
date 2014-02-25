package okeanos.runner.internal;

import javax.inject.Inject;

import okeanos.platform.PlatformService;
import okeanos.platform.services.PlatformManagementService;

import org.springframework.stereotype.Component;

@Component
public class Runner {
	@Inject
	private PlatformService platformService;

	@Inject
	private PlatformManagementService platformManagementService;
}
