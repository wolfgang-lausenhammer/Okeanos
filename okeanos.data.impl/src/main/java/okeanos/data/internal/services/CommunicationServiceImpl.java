package okeanos.data.internal.services;

import javax.inject.Inject;

import okeanos.data.CommunicationService;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("communicationServiceImpl")
public class CommunicationServiceImpl implements CommunicationService {
	@Inject
	private ApplicationContext context;
}
