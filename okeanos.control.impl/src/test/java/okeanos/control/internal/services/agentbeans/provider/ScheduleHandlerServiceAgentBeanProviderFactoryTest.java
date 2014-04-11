package okeanos.control.internal.services.agentbeans.provider;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The Class ScheduleHandlerServiceAgentBeanProviderFactoryTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ScheduleHandlerServiceAgentBeanProviderFactoryTest {

	/** The provider. */
	@Mock
	private Provider<ScheduleHandlerServiceAgentBean> provider;

	/** The schedule handler service agent bean provider factory. */
	private ScheduleHandlerServiceAgentBeanProviderFactory scheduleHandlerServiceAgentBeanProviderFactory;

	/** The Constant SCHEDULE_HANDLER_SERVICE_AGENT_BEAN. */
	private static final String SCHEDULE_HANDLER_SERVICE_AGENT_BEAN = "my-agent-bean";

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		scheduleHandlerServiceAgentBeanProviderFactory = new ScheduleHandlerServiceAgentBeanProviderFactory(
				SCHEDULE_HANDLER_SERVICE_AGENT_BEAN);

		scheduleHandlerServiceAgentBeanProviderFactory.setBeanFactory(null);
	}

	/**
	 * Test create instance.
	 */
	@Test
	public void testCreateInstance() {
		Provider<ScheduleHandlerServiceAgentBean> provider = scheduleHandlerServiceAgentBeanProviderFactory
				.createInstance();

		assertThat(provider, is(notNullValue()));
	}

}
