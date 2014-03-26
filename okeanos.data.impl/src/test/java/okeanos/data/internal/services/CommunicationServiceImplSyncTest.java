package okeanos.data.internal.services;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.inject.Provider;

import okeanos.data.internal.services.communication.MyTestMessage;
import okeanos.data.services.UUIDGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sercho.masp.space.SimpleObjectSpace;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.IMessageBoxAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.directory.IDirectory;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.lifecycle.LifecycleException;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

public class CommunicationServiceImplSyncTest {

	private static final String RECEIVER_AGENT_ADDRESS = "msgbox:receiver-agent-message-box-address";

	private static final String SENDER_AGENT_ADDRESS = "msgbox:sender-agent-message-box-address";

	private static final String TEST_UUID = "random-number-1";

	@Mock
	private ICommunicationBean communicationBean;

	@Mock
	private Provider<ICommunicationBean> communicationBeanProvider;

	private CommunicationServiceImpl communicationServiceImpl;

	@Mock
	private IDirectory directory;

	@Mock
	private AgentDescription receiverAgentDescription;

	@Mock
	private IMessageBoxAddress receiverMessageBoxAddress;

	@Mock
	private AbstractAgentBean sender;

	@Mock
	private Agent senderAgent;

	@Mock
	private AgentDescription senderAgentDescription;

	@Mock
	private IMemory senderMemory;

	@Mock
	private IMessageBoxAddress senderMessageBoxAddress;

	@Mock
	private UUIDGenerator uuidGenerator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(communicationBeanProvider.get()).thenReturn(communicationBean);

		// Sender Agent
		when(senderMessageBoxAddress.toString()).thenReturn(
				SENDER_AGENT_ADDRESS);
		when(senderAgentDescription.getMessageBoxAddress()).thenReturn(
				senderMessageBoxAddress);
		ReflectionTestUtils.setField(sender, "thisAgent", senderAgent);
		ReflectionTestUtils.setField(sender, "memory", senderMemory);
		when(senderAgent.getAgentDescription()).thenReturn(
				senderAgentDescription);
		senderAgent.setDirectory(directory);

		// Receiver
		when(receiverMessageBoxAddress.toString()).thenReturn(
				RECEIVER_AGENT_ADDRESS);
		when(receiverAgentDescription.getMessageBoxAddress()).thenReturn(
				receiverMessageBoxAddress);
		when(receiverAgentDescription.getName()).thenReturn("test-receiver");

		// UUID
		when(uuidGenerator.generateUUID()).thenReturn(TEST_UUID);

		communicationServiceImpl = new CommunicationServiceImpl(
				communicationBeanProvider, uuidGenerator);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testSendIAgentBeanICommunicationAddressIFact()
			throws CommunicationException, LifecycleException {
		MyTestMessage sentMessage = new MyTestMessage("my test content - send");
		MyTestMessage receivedMessage = new MyTestMessage(
				"my test content - receive");
		final WriteCallEvent<IJiacMessage> wce = new WriteCallEvent(
				new SimpleObjectSpace<IJiacMessage>("myspace"),
				new JiacMessage(receivedMessage));

		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(senderMemory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.send(sender,
				receiverMessageBoxAddress, sentMessage);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testSendIAgentBeanStringIFact()
			throws CommunicationException {
		MyTestMessage sentMessage = new MyTestMessage("my test content - send");
		MyTestMessage receivedMessage = new MyTestMessage(
				"my test content - receive");
		final WriteCallEvent<IJiacMessage> wce = new WriteCallEvent(
				new SimpleObjectSpace<IJiacMessage>("myspace"),
				new JiacMessage(receivedMessage));

		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(senderMemory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		when(directory.searchAllAgents(any(IAgentDescription.class)))
				.thenReturn(
						Arrays.asList((IAgentDescription) receiverAgentDescription));

		IJiacMessage answer = communicationServiceImpl.send(sender,
				"test-receiver", sentMessage);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));
	}
}
