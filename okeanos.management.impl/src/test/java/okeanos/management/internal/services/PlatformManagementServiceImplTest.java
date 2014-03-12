package okeanos.management.internal.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.inject.Provider;

import okeanos.management.internal.services.platformmanagement.OkeanosBasicAgentNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public class PlatformManagementServiceImplTest {
	private static final String UUID = "123";

	private PlatformManagementServiceImpl managementManagementService;

	@Mock
	private Provider<OkeanosBasicAgentNode> agentNodeProvider;

	@Mock
	private OkeanosBasicAgentNode mockAgentNode;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtils.setField(mockAgentNode, "uuid", UUID);
		when(agentNodeProvider.get()).thenReturn(mockAgentNode);
	}

	@Test
	public final void testStartAgentNode() throws LifecycleException {
		// setup
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);

		// do
		IAgentNode agentNode = managementManagementService.startAgentNode();

		// verify
		verify(agentNodeProvider).get();
		assertThat(mockAgentNode, sameInstance(agentNode));
		assertThat(mockAgentNode.getUUID(), equalTo(UUID));
	}

	@Test
	public final void testStopAgentNode() throws LifecycleException {
		// setup
		ReflectionTestUtils
				.setField(mockAgentNode, "agents", new ArrayList<>());
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);
		IAgentNode agentNode = managementManagementService.startAgentNode();

		// do
		managementManagementService.stopAgentNode(agentNode);

		// verify
		verify(mockAgentNode).shutdown();
	}

	@Test
	public final void testGetAgentNodeNull() {
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);

		IAgentNode node = managementManagementService.getAgentNode(UUID);

		assertThat(node, nullValue());
	}

	@Test
	public final void testGetAgentNodeNotNull() throws LifecycleException {
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);
		IAgentNode agentNode = managementManagementService.startAgentNode();

		IAgentNode node = managementManagementService.getAgentNode(UUID);

		assertThat(node, notNullValue());
		assertThat(node, sameInstance(agentNode));
	}

	@Test
	public final void testGetDefaultAgentNodeNull() {
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);

		IAgentNode node = managementManagementService.getDefaultAgentNode();

		assertThat(node, notNullValue());
		assertThat(node, sameInstance((IAgentNode) mockAgentNode));
	}

	@Test
	public final void testGetDefaultAgentNodeFirstAgentNodeofOneAgentNode()
			throws LifecycleException {
		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);
		IAgentNode agentNode = managementManagementService.startAgentNode();

		IAgentNode node = managementManagementService.getDefaultAgentNode();

		assertThat(node, notNullValue());
		assertThat(node, sameInstance(agentNode));
	}

	@Test
	public final void testGetDefaultAgentNodeFirstAgentNodeOfTwoAgentNodes()
			throws LifecycleException {
		OkeanosBasicAgentNode mockAgentNode2 = mock(OkeanosBasicAgentNode.class);
		ReflectionTestUtils.setField(mockAgentNode2, "uuid", UUID + "_OTHER");
		when(agentNodeProvider.get()).thenReturn(mockAgentNode, mockAgentNode2);

		managementManagementService = new PlatformManagementServiceImpl(
				agentNodeProvider);
		IAgentNode agentNode1 = managementManagementService.startAgentNode();
		IAgentNode agentNode2 = managementManagementService.startAgentNode();

		IAgentNode node = managementManagementService.getDefaultAgentNode();

		assertThat(node, notNullValue());
		assertThat(node,
				anyOf(sameInstance(agentNode1), sameInstance(agentNode2)));
	}

}
