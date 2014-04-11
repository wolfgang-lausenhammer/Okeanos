package okeanos.data.internal.services.agentbeans;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import okeanos.data.internal.services.agentbeans.communication.MyTestMessage;
import okeanos.data.services.UUIDGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sercho.masp.space.SimpleObjectSpace;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
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

/**
 * The Class CommunicationServiceImplSyncTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommunicationServiceImplSyncTest {

	/** The Constant MY_TEST_HEADER. */
	private static final String MY_TEST_HEADER = "my-test-header";

	/** The Constant MY_TEST_HEADER_VALUE. */
	private static final String MY_TEST_HEADER_VALUE = "my-test-header-value";

	/** The Constant RECEIVER_AGENT_ADDRESS. */
	private static final String RECEIVER_AGENT_ADDRESS = "msgbox:receiver-agent-message-box-address";

	/** The Constant SENDER_AGENT_ADDRESS. */
	private static final String SENDER_AGENT_ADDRESS = "msgbox:sender-agent-message-box-address";

	/** The Constant TEST_UUID. */
	private static final String TEST_UUID = "random-number-1";

	/** The communication bean. */
	@Mock
	private ICommunicationBean communicationBean;

	/** The communication bean provider. */
	@Mock
	private Provider<ICommunicationBean> communicationBeanProvider;

	/** The communication service impl. */
	private CommunicationServiceAgentBeanImpl communicationServiceImpl;

	/** The directory. */
	@Mock
	private IDirectory directory;

	/** The receiver agent description. */
	@Mock
	private AgentDescription receiverAgentDescription;

	/** The receiver message box address. */
	@Mock
	private IMessageBoxAddress receiverMessageBoxAddress;

	/** The sender agent. */
	@Mock
	private Agent senderAgent;

	/** The sender agent description. */
	@Mock
	private AgentDescription senderAgentDescription;

	/** The sender memory. */
	@Mock
	private IMemory senderMemory;

	/** The sender message box address. */
	@Mock
	private IMessageBoxAddress senderMessageBoxAddress;

	/** The uuid generator. */
	@Mock
	private UUIDGenerator uuidGenerator;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(communicationBeanProvider.get()).thenReturn(communicationBean);

		// Sender Agent
		when(senderMessageBoxAddress.toString()).thenReturn(
				SENDER_AGENT_ADDRESS);
		when(senderAgentDescription.getMessageBoxAddress()).thenReturn(
				senderMessageBoxAddress);
		when(senderAgent.getAgentDescription()).thenReturn(
				senderAgentDescription);
		senderAgent.setDirectory(directory);
		senderAgent.setMemory(senderMemory);

		// Receiver
		when(receiverMessageBoxAddress.toString()).thenReturn(
				RECEIVER_AGENT_ADDRESS);
		when(receiverAgentDescription.getMessageBoxAddress()).thenReturn(
				receiverMessageBoxAddress);
		when(receiverAgentDescription.getName()).thenReturn("test-receiver");

		// UUID
		when(uuidGenerator.generateUUID()).thenReturn(TEST_UUID);

		communicationServiceImpl = new CommunicationServiceAgentBeanImpl(
				communicationBeanProvider, uuidGenerator);
		communicationServiceImpl.setThisAgent(senderAgent);
	}

	/**
	 * Test send i agent bean i communication address i fact.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 * @throws LifecycleException
	 *             the lifecycle exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(senderMemory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.send(
				receiverMessageBoxAddress, sentMessage);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));

		ArgumentCaptor<IJiacMessage> jiacMessageArg = ArgumentCaptor
				.forClass(IJiacMessage.class);
		ArgumentCaptor<ICommunicationAddress> communicationAddressArg = ArgumentCaptor
				.forClass(ICommunicationAddress.class);
		verify(communicationBean).send(jiacMessageArg.capture(),
				communicationAddressArg.capture());
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationCorrelationId"),
				equalTo(TEST_UUID));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationSender"),
				equalTo(SENDER_AGENT_ADDRESS));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationReceiver"),
				equalTo(RECEIVER_AGENT_ADDRESS));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
	}

	/**
	 * Test send i agent bean i communication address i fact map.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 * @throws LifecycleException
	 *             the lifecycle exception
	 */
	@Test
	public final void testSendIAgentBeanICommunicationAddressIFactMap()
			throws CommunicationException, LifecycleException {
		MyTestMessage sentMessage = new MyTestMessage("my test content - send");
		MyTestMessage receivedMessage = new MyTestMessage(
				"my test content - receive");
		final WriteCallEvent<IJiacMessage> wce = new WriteCallEvent(
				new SimpleObjectSpace<IJiacMessage>("myspace"),
				new JiacMessage(receivedMessage));

		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(senderMemory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));
		Map<String, String> headers = new HashMap<>();
		headers.put(MY_TEST_HEADER, MY_TEST_HEADER_VALUE);

		IJiacMessage answer = communicationServiceImpl.send(
				receiverMessageBoxAddress, sentMessage, headers);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));

		ArgumentCaptor<IJiacMessage> jiacMessageArg = ArgumentCaptor
				.forClass(IJiacMessage.class);
		ArgumentCaptor<ICommunicationAddress> communicationAddressArg = ArgumentCaptor
				.forClass(ICommunicationAddress.class);
		verify(communicationBean).send(jiacMessageArg.capture(),
				communicationAddressArg.capture());
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationCorrelationId"),
				equalTo(TEST_UUID));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationSender"),
				equalTo(SENDER_AGENT_ADDRESS));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationReceiver"),
				equalTo(RECEIVER_AGENT_ADDRESS));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
		assertThat(jiacMessageArg.getValue().getHeader(MY_TEST_HEADER),
				is(equalTo(MY_TEST_HEADER_VALUE)));
	}

	/**
	 * Test send i agent bean string i fact.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
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

		IJiacMessage answer = communicationServiceImpl.send("test-receiver",
				sentMessage);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));

		ArgumentCaptor<IJiacMessage> jiacMessageArg = ArgumentCaptor
				.forClass(IJiacMessage.class);
		ArgumentCaptor<ICommunicationAddress> communicationAddressArg = ArgumentCaptor
				.forClass(ICommunicationAddress.class);
		verify(communicationBean).send(jiacMessageArg.capture(),
				communicationAddressArg.capture());
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationCorrelationId"),
				equalTo(TEST_UUID));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationSender"),
				equalTo(SENDER_AGENT_ADDRESS));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationReceiver"),
				equalTo(RECEIVER_AGENT_ADDRESS));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
	}

	/**
	 * Test send i agent bean string i fact map.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendIAgentBeanStringIFactMap()
			throws CommunicationException {
		MyTestMessage sentMessage = new MyTestMessage("my test content - send");
		MyTestMessage receivedMessage = new MyTestMessage(
				"my test content - receive");
		final WriteCallEvent<IJiacMessage> wce = new WriteCallEvent(
				new SimpleObjectSpace<IJiacMessage>("myspace"),
				new JiacMessage(receivedMessage));

		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
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
		Map<String, String> headers = new HashMap<>();
		headers.put(MY_TEST_HEADER, MY_TEST_HEADER_VALUE);

		IJiacMessage answer = communicationServiceImpl.send("test-receiver",
				sentMessage, headers);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(senderMemory, atLeastOnce()).detach(any(SpaceObserver.class));

		ArgumentCaptor<IJiacMessage> jiacMessageArg = ArgumentCaptor
				.forClass(IJiacMessage.class);
		ArgumentCaptor<ICommunicationAddress> communicationAddressArg = ArgumentCaptor
				.forClass(ICommunicationAddress.class);
		verify(communicationBean).send(jiacMessageArg.capture(),
				communicationAddressArg.capture());
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationCorrelationId"),
				equalTo(TEST_UUID));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationSender"),
				equalTo(SENDER_AGENT_ADDRESS));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationReceiver"),
				equalTo(RECEIVER_AGENT_ADDRESS));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
		assertThat(jiacMessageArg.getValue().getHeader(MY_TEST_HEADER),
				is(equalTo(MY_TEST_HEADER_VALUE)));
	}
}
