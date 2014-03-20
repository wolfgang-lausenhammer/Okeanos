package okeanos.data.internal.services;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.inject.Provider;

import okeanos.data.services.UUIDGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
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

public class CommunicationServiceImplAsyncTest {

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

	@Test
	public final void testSendAsyncIAgentBeanICommunicationAddressIFact()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");

		communicationServiceImpl.sendAsync(sender, receiverMessageBoxAddress,
				message);

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

	@Test
	public final void testSendAsyncIAgentBeanStringIFact()
			throws CommunicationException {
		MyTestMessage message = new MyTestMessage("my test content");
		when(directory.searchAllAgents(any(IAgentDescription.class)))
				.thenReturn(
						Arrays.asList((IAgentDescription) receiverAgentDescription));

		communicationServiceImpl.sendAsync(sender, "test-receiver", message);

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
}
