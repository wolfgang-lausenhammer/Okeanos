package okeanos.control.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;
import okeanos.control.services.agentbeans.provider.ControlServicesProvider;

import org.springframework.stereotype.Component;

/**
 * Provides methods for creating instances of the control services.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("controlServicesProvider")
public class ControlServicesProviderImpl implements ControlServicesProvider {

	/** The schedule handler service agent bean provider. */
	private Provider<ScheduleHandlerServiceAgentBean> scheduleHandlerServiceAgentBeanProvider;

	/**
	 * Instantiates a new control services provider impl.
	 * 
	 * @param providerCreatingFactoryBean
	 *            the provider creating factory bean
	 */
	@Inject
	public ControlServicesProviderImpl(
			ScheduleHandlerServiceAgentBeanProviderFactory providerCreatingFactoryBean) {
		this.scheduleHandlerServiceAgentBeanProvider = providerCreatingFactoryBean
				.createInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.control.services.agentbeans.provider.ControlServicesProvider#
	 * getNewScheduleHandlerServiceAgentBean()
	 */
	@Override
	public ScheduleHandlerServiceAgentBean getNewScheduleHandlerServiceAgentBean() {
		return scheduleHandlerServiceAgentBeanProvider.get();
	}

}
