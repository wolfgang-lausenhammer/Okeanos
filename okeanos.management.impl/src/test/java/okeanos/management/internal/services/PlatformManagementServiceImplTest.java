package okeanos.management.internal.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Provider;

import okeanos.management.internal.services.PlatformManagementServiceImpl;
import okeanos.management.internal.services.platformmanagement.OkeanosBasicAgentNode;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Equality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:/application-context.xml")
public class PlatformManagementServiceImplTest {

	// @InjectMocks
	private PlatformManagementServiceImpl managementManagementService;

	// @Inject
	// @Mock
	private Provider<OkeanosBasicAgentNode> agentNodeProvider;

	@Mock
	OkeanosBasicAgentNode mockAgentNode;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void testStartAgentNode() throws LifecycleException {
		// setup
		String uuid = "123";
		ReflectionTestUtils.setField(mockAgentNode, "uuid", uuid);
		agentNodeProvider = mock(Provider.class);
		when(agentNodeProvider.get()).thenReturn(mockAgentNode);
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);

		// do
		IAgentNode agentNode = managementManagementService.startAgentNode();

		// verify
		verify(agentNodeProvider).get();
		assertThat(mockAgentNode, sameInstance(agentNode));
		assertThat(mockAgentNode.getUUID(), equalTo(uuid));
	}

	@Test
	public final void testStopAgentNode() {
		// fail("Not yet implemented");
	}

	@Test
	public final void testGetAgentNode() {
		// fail("Not yet implemented");
	}

	@Test
	public final void testGetDefaultAgentNode() {
		// fail("Not yet implemented");
	}

}
