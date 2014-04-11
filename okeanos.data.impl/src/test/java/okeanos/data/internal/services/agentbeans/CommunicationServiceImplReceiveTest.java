package okeanos.data.internal.services.agentbeans;

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

import okeanos.data.internal.services.agentbeans.communication.MyTestMessage;
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

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.comm.ICommunicationBean;
import de.dailab.jiactng.agentcore.comm.message.IJiacMessage;
import de.dailab.jiactng.agentcore.comm.message.JiacMessage;
import de.dailab.jiactng.agentcore.knowledge.IFact;
import de.dailab.jiactng.agentcore.knowledge.IMemory;

/**
 * The Class CommunicationServiceImplReceiveTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class CommunicationServiceImplReceiveTest {

	/** The communication bean. */
	@Mock
	private ICommunicationBean communicationBean;

	/** The communication bean provider. */
	@Mock
	private Provider<ICommunicationBean> communicationBeanProvider;

	/** The communication service impl. */
	private CommunicationServiceAgentBeanImpl communicationServiceImpl;

	/** The memory. */
	@Mock
	private IMemory memory;

	/** The receiver. */
	@Mock
	private Agent receiver;

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
		receiver.setMemory(memory);

		communicationServiceImpl = new CommunicationServiceAgentBeanImpl(
				communicationBeanProvider, uuidGenerator);
		communicationServiceImpl.setThisAgent(receiver);
	}

	/**
	 * Test receive message async i agent bean.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ExecutionException
	 *             the execution exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessageAsync()
				.get();

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	/**
	 * Test receive message async i agent bean i fact.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ExecutionException
	 *             the execution exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
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
				new MyTestMessage(null)).get();

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	/**
	 * Test receive message callback i agent bean space observer of i fact.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		communicationServiceImpl
				.receiveMessageCallback(new SpaceObserver<IFact>() {
					@Override
					public void notify(final SpaceEvent<? extends IFact> event) {
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

	/**
	 * Test receive message callback i agent bean space observer of i fact i
	 * fact.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
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

		communicationServiceImpl.receiveMessageCallback(
				new SpaceObserver<IFact>() {
					@Override
					public void notify(final SpaceEvent<? extends IFact> event) {
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

	/**
	 * Test receive message i agent bean.
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl.receiveMessage();

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}

	/**
	 * Test receive message i agent bean i fact.
	 */
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
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				SpaceObserver<IJiacMessage> callback = (SpaceObserver<IJiacMessage>) invocation
						.getArguments()[0];

				callback.notify(wce);
				return null;
			}
		})
				.when(memory)
				.attach(Matchers.any(SpaceObserver.class),
						Matchers.any(IFact.class));

		IJiacMessage answer = communicationServiceImpl
				.receiveMessage(new MyTestMessage(null));

		assertThat(answer.getPayload(), sameInstance((IFact) receivedMessage));
		verify(memory, atLeastOnce()).detach(any(SpaceObserver.class));
	}
}
