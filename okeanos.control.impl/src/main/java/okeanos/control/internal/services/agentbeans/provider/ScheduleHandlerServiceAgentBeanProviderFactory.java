package okeanos.control.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ProviderCreatingFactoryBean;
import org.springframework.stereotype.Component;

/**
 * A factory for creating ScheduleHandlerServiceAgentBeanProvider objects.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component
public class ScheduleHandlerServiceAgentBeanProviderFactory extends
		ProviderCreatingFactoryBean {

	/**
	 * Instantiates a new schedule handler service agent bean provider factory.
	 * 
	 * @param scheduleHandlerServiceAgentBean
	 *            the schedule handler service agent bean
	 */
	@Inject
	public ScheduleHandlerServiceAgentBeanProviderFactory(
			@Value("sendOwnScheduleOnlyScheduleHandlerServiceAgentBean") final String scheduleHandlerServiceAgentBean) {
		setTargetBeanName(scheduleHandlerServiceAgentBean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.config.ProviderCreatingFactoryBean#
	 * createInstance()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Provider createInstance() {
		return super.createInstance();
	}

}
