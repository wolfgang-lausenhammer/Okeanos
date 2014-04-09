package okeanos.control.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.provider.ControlServicesProvider;

import org.springframework.stereotype.Component;

@Component("controlServicesProvider")
public class ControlServicesProviderImpl implements ControlServicesProvider {

	private Provider<ScheduleHandlerServiceAgentBean> scheduleHandlerServiceAgentBeanProvider;

	@Inject
	public ControlServicesProviderImpl(
			Provider<ScheduleHandlerServiceAgentBean> scheduleHandlerServiceAgentBeanProvider) {
		this.scheduleHandlerServiceAgentBeanProvider = scheduleHandlerServiceAgentBeanProvider;
	}

	@Override
	public ScheduleHandlerServiceAgentBean getNewScheduleHandlerServiceAgentBean() {
		return scheduleHandlerServiceAgentBeanProvider.get();
	}

}
