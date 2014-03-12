package okeanos.management.internal.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import okeanos.core.entities.Entity;
import okeanos.core.entities.builder.EntityBuilder;
import okeanos.management.internal.services.entitymanagement.OkeanosBasicAgent;
import okeanos.management.services.PlatformManagementService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.IAgent;
import de.dailab.jiactng.agentcore.IAgentNode;
import de.dailab.jiactng.agentcore.lifecycle.DefaultLifecycleHandler;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;

public class EntityManagementServiceImplTest {
	private static final String UUID = "123";

	@Mock
	private Provider<OkeanosBasicAgent> agentProvider;

	@Mock
	private EntityBuilder entityBuilder;

	@Mock
	private Provider<EntityBuilder> entityBuilderProvider;

	private EntityManagementServiceImpl entityManagementService;

	@Mock
	private OkeanosBasicAgent mockAgent;

	@Mock
	private PlatformManagementService platformManagementService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(agentProvider.get()).thenReturn(mockAgent);
		when(entityBuilderProvider.get()).thenReturn(entityBuilder);
		ReflectionTestUtils.setField(mockAgent, "agentId", UUID + "-agent");

		entityManagementService = new EntityManagementServiceImpl(
				platformManagementService, entityBuilderProvider, agentProvider);
	}

	@Test
	public void testGetEntity() throws LifecycleException {
		IAgentNode node = mock(IAgentNode.class);
		Entity entity = mock(Entity.class);
		IAgent agent = mock(IAgent.class);
		when(agent.getAgentId()).thenReturn(UUID + "-agent");
		when(entity.getId()).thenReturn(UUID);
		when(entity.getAgent()).thenReturn(agent);
		entityManagementService.startEntity(entity, node);

		Entity returnedEntity = entityManagementService.getEntity(UUID);

		assertThat(returnedEntity, notNullValue());
		assertThat(returnedEntity, equalTo(entity));
	}

	@Test
	public void testGetEntityNull() {
		Entity entity = entityManagementService.getEntity(UUID);

		assertThat(entity, nullValue());
	}

	@Test
	public void testLoadConfigurableEntity() {

		EntityBuilder builder = entityManagementService
				.loadConfigurableEntity();

		assertThat(builder, notNullValue());
		assertThat(builder, sameInstance(entityBuilder));
	}

	@Test
	public void testLoadEntity() {

		Entity entity = entityManagementService.loadEntity();

		assertThat(entity, nullValue());
		verify(entityBuilder).agent(any(IAgent.class));
		verify(entityBuilder).build();
	}

	@Test
	public void testLoadEntityFromJson() {
		String entityAsJson = "anything";

		Entity entity = entityManagementService
				.loadEntityFromJson(entityAsJson);

		verify(entityBuilder).fromJson(entityAsJson);
		assertThat(entity, nullValue());
	}

	@Test(expected = NullPointerException.class)
	public void testLoadEntityFromJsonNull() {
		entityManagementService.loadEntityFromJson(null);
	}

	@Test
	public void testSaveEntityToJson() {
		Entity entity = mock(Entity.class);
		when(entity.getId()).thenReturn(UUID);

		String json = entityManagementService.saveEntityToJson(entity);

		assertThat(json, notNullValue());
		assertThat(json, equalTo("{\"id\":\"" + UUID + "\"}"));
	}

	@Test
	public void testStartEntityEntity() throws LifecycleException {
		IAgentNode node = mock(IAgentNode.class);
		when(platformManagementService.getDefaultAgentNode()).thenReturn(node);
		Entity entity = mock(Entity.class);
		IAgent agent = mock(IAgent.class);
		when(agent.getAgentId()).thenReturn(UUID + "-agent");
		when(entity.getId()).thenReturn(UUID);
		when(entity.getAgent()).thenReturn(agent);

		Entity returnedEntity = entityManagementService.startEntity(entity);

		verify(node).addAgent(agent);
		assertThat(returnedEntity, equalTo(entity));
		assertThat(entityManagementService.getEntity(UUID), equalTo(entity));
	}

	@Test(expected = NullPointerException.class)
	public void testStartEntityEntityEntityNull() throws LifecycleException {
		entityManagementService.startEntity(null);
	}

	@Test
	public void testStartEntityEntityIAgentNode() throws LifecycleException {
		IAgentNode node = mock(IAgentNode.class);
		Entity entity = mock(Entity.class);
		IAgent agent = mock(IAgent.class);
		when(agent.getAgentId()).thenReturn(UUID + "-agent");
		when(entity.getId()).thenReturn(UUID);
		when(entity.getAgent()).thenReturn(agent);

		Entity returnedEntity = entityManagementService.startEntity(entity,
				node);

		verify(node).addAgent(agent);
		assertThat(returnedEntity, equalTo(entity));
		assertThat(entityManagementService.getEntity(UUID), equalTo(entity));
	}

	@Test(expected = NullPointerException.class)
	public void testStartEntityEntityIAgentNodeEntityAndIAgentNodeNull()
			throws LifecycleException {

		entityManagementService.startEntity(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testStartEntityEntityIAgentNodeEntityNull()
			throws LifecycleException {
		IAgentNode node = mock(IAgentNode.class);

		entityManagementService.startEntity(null, node);
	}

	@Test(expected = NullPointerException.class)
	public void testStartEntityEntityIAgentNodeIAgentNodeNull()
			throws LifecycleException {
		Entity entity = mock(Entity.class);

		entityManagementService.startEntity(entity, null);
	}

	@Test
	public void testStopEntity() throws LifecycleException {
		Entity entity = mock(Entity.class);
		IAgent agent = mock(IAgent.class);
		when(entity.getAgent()).thenReturn(agent);

		entityManagementService.stopEntity(entity);

		verify(entity).getAgent();
		verify(agent).stop();
	}

	@Test
	public void testUnloadEntity() throws LifecycleException {
		Entity entity = mock(Entity.class);
		when(entity.getId()).thenReturn(UUID);
		when(entity.getAgent()).thenReturn(mockAgent);
		DefaultLifecycleHandler lifecycle = mock(DefaultLifecycleHandler.class);
		ReflectionTestUtils.setField(mockAgent, "lifecycle", lifecycle);

		entityManagementService.unloadEntity(entity);

		verify(entity).getAgent();
		verify(lifecycle).beforeStop();
		verify(lifecycle).beforeCleanup();
	}

}
