package okeanos.control.services.agentbeans.provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;

/**
 * Provides methods for creating instances of the control services.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface ControlServicesProvider {

	/**
	 * Gets the new schedule handler service agent bean.
	 * 
	 * @return the new schedule handler service agent bean
	 */
	ScheduleHandlerServiceAgentBean getNewScheduleHandlerServiceAgentBean();
}
