package okeanos.data.internal.services.agentbeans;

import static okeanos.data.services.agentbeans.CommunicationServiceAgentBean.Header.COMMUNICATION_CORRELATION_ID;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import okeanos.data.internal.services.agentbeans.communication.MyTestMessage;
import okeanos.data.services.UUIDGenerator;
import okeanos.data.services.agentbeans.entities.GridFact;
import okeanos.data.services.agentbeans.entities.GroupFact;
import okeanos.data.services.entities.MessageScope;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.comm.CommunicationException;
import de.dailab.jiactng.agentcore.comm.ICommunicationAddress;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.IMessageBoxAddress;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.directory.IDirectory;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.ontology.AgentDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

// TODO: Auto-generated Javadoc
/**
 * The Class CommunicationServiceImplAsyncTest.
 */
public class CommunicationServiceImplAsyncTest {

	/** The Constant MY_CORRELATION_ID. */
	private static final String MY_CORRELATION_ID = "my-correlation-id";

	/** The Constant MY_TEST_HEADER. */
	private static final String MY_TEST_HEADER = "my-test-header";

	/** The Constant MY_TEST_HEADER_VALUE. */
	private static final String MY_TEST_HEADER_VALUE = "my-test-header-value";

	/** The Constant RECEIVER_AGENT_ADDRESS. */
	private static final String RECEIVER_AGENT_ADDRESS = "msgbox:receiver-agent-message-box-address";

	/** The Constant RECEIVER_AGENT_NAME. */
	private static final String RECEIVER_AGENT_NAME = "test-receiver";

	/** The Constant SENDER_AGENT_ADDRESS. */
	private static final String SENDER_AGENT_ADDRESS = "msgbox:sender-agent-message-box-address";

	/** The Constant TEST_UUID. */
	private static final String TEST_UUID = "random-number-1";

	/** The Constant GROUP_ID. */
	private static final String GROUP_ID = "my-group-id";

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
		when(receiverAgentDescription.getName())
				.thenReturn(RECEIVER_AGENT_NAME);

		// UUID
		when(uuidGenerator.generateUUID()).thenReturn(TEST_UUID);

		communicationServiceImpl = new CommunicationServiceAgentBeanImpl(
				communicationBeanProvider, uuidGenerator);
		communicationServiceImpl.setThisAgent(senderAgent);
		communicationServiceImpl.setMemory(senderMemory);
	}

	/**
	 * Test send async i agent bean i communication address i fact.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendAsyncIAgentBeanICommunicationAddressIFact()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");

		communicationServiceImpl.sendAsync(receiverMessageBoxAddress, message);

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
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
	}

	/**
	 * Test send async i agent bean i communication address i fact map.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendAsyncIAgentBeanICommunicationAddressIFactMap()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");
		Map<String, String> headers = new HashMap<>();
		headers.put(MY_TEST_HEADER, MY_TEST_HEADER_VALUE);

		communicationServiceImpl.sendAsync(receiverMessageBoxAddress, message,
				headers);

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
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
		assertThat(jiacMessageArg.getValue().getHeader(MY_TEST_HEADER),
				is(equalTo(MY_TEST_HEADER_VALUE)));
	}

	/**
	 * Test send async i agent bean string i fact.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendAsyncIAgentBeanStringIFact()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");
		when(directory.searchAllAgents(any(IAgentDescription.class)))
				.thenReturn(
						Arrays.asList((IAgentDescription) receiverAgentDescription));

		communicationServiceImpl.sendAsync("test-receiver", message);

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
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
	}

	/**
	 * Test send async i agent bean string i fact map.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendAsyncIAgentBeanStringIFactMap()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");
		when(directory.searchAllAgents(any(IAgentDescription.class)))
				.thenReturn(
						Arrays.asList((IAgentDescription) receiverAgentDescription));
		Map<String, String> headers = new HashMap<>();
		headers.put(MY_TEST_HEADER, MY_TEST_HEADER_VALUE);

		communicationServiceImpl.sendAsync("test-receiver", message, headers);

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
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
		assertThat(jiacMessageArg.getValue().getHeader(MY_TEST_HEADER),
				is(equalTo(MY_TEST_HEADER_VALUE)));
	}

	/**
	 * Test send async i agent bean string i fact map existing correlation id.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public final void testSendAsyncIAgentBeanStringIFactMapExistingCorrelationId()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");
		when(directory.searchAllAgents(any(IAgentDescription.class)))
				.thenReturn(
						Arrays.asList((IAgentDescription) receiverAgentDescription));
		Map<String, String> headers = new HashMap<>();
		headers.put(COMMUNICATION_CORRELATION_ID, MY_CORRELATION_ID);

		communicationServiceImpl.sendAsync("test-receiver", message, headers);

		ArgumentCaptor<IJiacMessage> jiacMessageArg = ArgumentCaptor
				.forClass(IJiacMessage.class);
		ArgumentCaptor<ICommunicationAddress> communicationAddressArg = ArgumentCaptor
				.forClass(ICommunicationAddress.class);
		verify(communicationBean).send(jiacMessageArg.capture(),
				communicationAddressArg.capture());
		assertThat(
				jiacMessageArg.getValue().getHeader(
						"OkeanosCommunicationCorrelationId"),
				equalTo(MY_CORRELATION_ID));
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
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
		assertThat(
				jiacMessageArg.getValue().getHeader(
						COMMUNICATION_CORRELATION_ID),
				is(equalTo(MY_CORRELATION_ID)));
	}

	/**
	 * Test broadcast group.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public void testBroadcastGroup() throws CommunicationException {
		Set<GroupFact> groups = new HashSet<>();
		groups.add(new GroupFact(GROUP_ID));
		when(senderMemory.readAll(any(GroupFact.class))).thenReturn(groups);

		MyTestMessage message = new MyTestMessage("my test content");

		communicationServiceImpl.broadcast(MessageScope.GROUP, message);

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
						"OkeanosCommunicationReceiver"), equalTo("group:"
						+ GROUP_ID));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
	}

	/**
	 * Test broadcast multiple group.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public void testBroadcastMultipleGroup() throws CommunicationException {
		Set<GroupFact> groups = new HashSet<>();
		groups.add(new GroupFact(GROUP_ID));
		groups.add(new GroupFact(GROUP_ID + GROUP_ID));
		when(senderMemory.readAll(any(GroupFact.class))).thenReturn(groups);

		MyTestMessage message = new MyTestMessage("my test content");

		communicationServiceImpl.broadcast(MessageScope.GROUP, message);

		verify(communicationBean, times(2)).send(any(IJiacMessage.class),
				any(ICommunicationAddress.class));
	}

	/**
	 * Test broadcast grid.
	 * 
	 * @throws CommunicationException
	 *             the communication exception
	 */
	@Test
	public void testBroadcastGrid() throws CommunicationException {
		when(senderMemory.read(any(GridFact.class))).thenReturn(
				new GridFact(GROUP_ID));

		MyTestMessage message = new MyTestMessage("my test content");

		communicationServiceImpl.broadcast(MessageScope.GRID, message);

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
						"OkeanosCommunicationReceiver"), equalTo("group:"
						+ GROUP_ID));
		assertThat(jiacMessageArg.getValue().getSender().toString(),
				equalTo(senderMessageBoxAddress.toString()));
		assertThat(jiacMessageArg.getValue().getPayload(),
				sameInstance((IFact) message));
	}
}
