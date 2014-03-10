package okeanos.runner.internal;

import javax.inject.Inject;

import okeanos.management.services.PlatformManagementService;

import org.springframework.stereotype.Component;

@Component
public class Runner {
	@Inject
	private PlatformManagementService managementManagementService;
}
