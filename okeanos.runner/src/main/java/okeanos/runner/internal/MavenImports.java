package okeanos.runner.internal;

import javax.management.NotificationBroadcasterSupport;
import javax.security.auth.Destroyable;

import okeanos.spring.misc.postprocessors.LoggingBeanFactoryPostProcessor;

import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;

import okeanos.control.algorithms.ControlAlgorithm;
import okeanos.data.services.agentbeans.CommunicationServiceAgentBean;
import de.dailab.jiactng.agentcore.comm.CommunicationBean;
import de.dailab.jiactng.agentcore.comm.broker.ActiveMQBroker;
import de.dailab.jiactng.agentcore.comm.transport.jms.JMSMessageTransport;
import de.dailab.jiactng.agentcore.conf.GenericAgentProperties;
import de.dailab.jiactng.agentcore.directory.DirectoryAgentNodeBean;
import de.dailab.jiactng.agentcore.execution.SimpleExecutionCycle;
import de.dailab.jiactng.agentcore.knowledge.Memory;
import de.dailab.jiactng.agentcore.management.jmx.JaasAuthenticator;

/**
 * The Class MavenImports.
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The agent node bean. */
	private DirectoryAgentNodeBean agentNodeBean;
	/** The authenticator. */
	private JaasAuthenticator authenticator;
	/** The autowired annotation bean post processor. */
	private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
	/** The broker. */
	private ActiveMQBroker broker;
	/** The common annotation bean post processor. */
	private CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;
	/** The communication bean. */
	private CommunicationBean communicationBean;
	/** The d. */
	private Destroyable d;
	/** The execution cycle. */
	private SimpleExecutionCycle executionCycle;
	/** The memory. */
	private Memory memory;
	/** The osgi service factory bean. */
	private OsgiServiceFactoryBean osgiServiceFactoryBean;
	/** The prop. */
	private GenericAgentProperties prop;
	/** The required annotation bean post processor. */
	private RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor;
	/** The logging bean factory post processor. */
	private LoggingBeanFactoryPostProcessor loggingBeanFactoryPostProcessor;
	/** The transport. */
	private JMSMessageTransport transport;
	/** The x. */
	private NotificationBroadcasterSupport x;
	/** The control algorithm. */
	private ControlAlgorithm controlAlgorithm;
	/** The communication service agent bean. */
	private CommunicationServiceAgentBean communicationServiceAgentBean;
	AsyncAnnotationBeanPostProcessor asyncAnnotationBeanPostProcessor;
	TaskExecutorFactoryBean taskExecutorFactoryBean;
	ThreadPoolTaskScheduler threadPoolTaskScheduler;
}
