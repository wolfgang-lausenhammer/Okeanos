package okeanos.control.internal.services.agentbeans.provider;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import okeanos.control.services.agentbeans.ScheduleHandlerServiceAgentBean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The Class ControlServicesProviderImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ControlServicesProviderImplTest {

	/** The control services provider impl. */
	private ControlServicesProviderImpl controlServicesProviderImpl;

	/** The provider creating factory bean. */
	@Mock
	private ScheduleHandlerServiceAgentBeanProviderFactory providerCreatingFactoryBean;

	/** The provider. */
	@Mock
	private Provider<ScheduleHandlerServiceAgentBean> provider;

	/** The schedule handler service agent bean. */
	@Mock
	private ScheduleHandlerServiceAgentBean scheduleHandlerServiceAgentBean;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(providerCreatingFactoryBean.createInstance()).thenReturn(provider);
		when(provider.get()).thenReturn(scheduleHandlerServiceAgentBean);

		controlServicesProviderImpl = new ControlServicesProviderImpl(
				providerCreatingFactoryBean);
	}

	/**
	 * Test get new schedule handler service agent bean.
	 */
	@Test
	public void testGetNewScheduleHandlerServiceAgentBean() {
		ScheduleHandlerServiceAgentBean scheduleHandlerServiceAgentBean = controlServicesProviderImpl
				.getNewScheduleHandlerServiceAgentBean();

		assertThat(scheduleHandlerServiceAgentBean,
				is(sameInstance(this.scheduleHandlerServiceAgentBean)));
	}

}
