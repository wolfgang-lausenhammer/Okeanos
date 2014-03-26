package okeanos.data.internal.services;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

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
import org.sercho.masp.space.event.SpaceEvent;
import org.sercho.masp.space.event.SpaceObserver;
import org.sercho.masp.space.event.WriteCallEvent;
import org.springframework.test.util.ReflectionTestUtils;

import de.dailab.jiactng.agentcore.AbstractAgentBean;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;

public class CommunicationServiceImplReceiveTest {

	@Mock
	private ICommunicationBean communicationBean;

	@Mock
	private Provider<ICommunicationBean> communicationBeanProvider;

	private CommunicationServiceImpl communicationServiceImpl;

	@Mock
	private AbstractAgentBean receiver;

	@Mock
	private IMemory memory;

	@Mock
	private UUIDGenerator uuidGenerator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(communicationBeanProvider.get()).thenReturn(communicationBean);

		// Sender Agent
		ReflectionTestUtils.setField(receiver, "memory", memory);

		communicationServiceImpl = new CommunicationServiceImpl(
				communicationBeanProvider, uuidGenerator);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testReceiveMessageIAgentBean() {
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
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessage(receiver);

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testReceiveMessageIAgentBeanIFact() {
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
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessage(receiver,
				new MyTestMessage(null));

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testReceiveMessageAsyncIAgentBean()
			throws InterruptedException, ExecutionException {
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
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessageAsync(
				receiver).get();

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testReceiveMessageAsyncIAgentBeanIFact()
			throws InterruptedException, ExecutionException {
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
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessageAsync(
				receiver, new MyTestMessage(null)).get();

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	@Test
	public final void testReceiveMessageCallbackIAgentBeanSpaceObserverOfIFact()
			throws InterruptedException {
		final BlockingQueue<IJiacMessage> queue = new ArrayBlockingQueue<>(1);
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
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		communicationServiceImpl.receiveMessageCallback(receiver,
				new SpaceObserver<IFact>() {
					@Override
					public void notify(SpaceEvent<? extends IFact> event) {
						try {
							queue.put(((WriteCallEvent<IJiacMessage>) event)
									.getObject());
						} catch (InterruptedException e) {
							fail("InterruptedException");
						}
					}
				});

		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
	}

	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	@Test
	public final void testReceiveMessageCallbackIAgentBeanSpaceObserverOfIFactIFact()
			throws InterruptedException {
		final BlockingQueue<IJiacMessage> queue = new ArrayBlockingQueue<>(5);
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

				// 5 messages!
				callback.notify(wce);
				callback.notify(wce);
				callback.notify(wce);
				callback.notify(wce);
				callback.notify(wce);
				return null;
			}
		})
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		communicationServiceImpl.receiveMessageCallback(receiver,
				new SpaceObserver<IFact>() {
					@Override
					public void notify(SpaceEvent<? extends IFact> event) {
						try {
							queue.put(((WriteCallEvent<IJiacMessage>) event)
									.getObject());
						} catch (InterruptedException e) {
							fail("InterruptedException");
						}
					}
				}, new MyTestMessage(null));

		// check for 5 messages
		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
		assertThat(queue.take().getPayload(),
				sameInstance((IFact) receivedMessage));
	}
}
