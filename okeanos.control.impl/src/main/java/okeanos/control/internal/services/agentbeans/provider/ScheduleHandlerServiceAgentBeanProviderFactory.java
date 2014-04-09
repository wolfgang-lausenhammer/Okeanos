package okeanos.control.internal.services.agentbeans.provider;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ProviderCreatingFactoryBean;
import org.springframework.stereotype.Component;

/**
 * A factory for creating ScheduleHandlerServiceAgentBeanProvider objects.
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
			@Value("sendOwnScheduleOnlyScheduleHandlerServiceAgentBean") String scheduleHandlerServiceAgentBean) {
		setTargetBeanName(scheduleHandlerServiceAgentBean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.config.ProviderCreatingFactoryBean#
	 * createInstance()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Provider<ScheduleHandlerServiceAgentBean> createInstance() {
		return super.createInstance();
	}

}
