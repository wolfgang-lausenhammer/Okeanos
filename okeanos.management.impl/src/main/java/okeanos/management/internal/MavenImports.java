package okeanos.management.internal;

import javax.management.NotificationBroadcasterSupport;
import javax.security.auth.Destroyable;

import okeanos.management.internal.spring.postprocessor.ChildOfBeanFactoryPostProcessor;
import okeanos.spring.misc.postprocessors.LoggingBeanFactoryPostProcessor;

import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

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
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The notification broadcaster support. */
	private NotificationBroadcasterSupport notificationBroadcasterSupport;
	/** The generic agent properties. */
	private GenericAgentProperties genericAgentProperties;
	/** The broker. */
	private ActiveMQBroker broker;
	/** The transport. */
	private JMSMessageTransport transport;
	/** The authenticator. */
	private JaasAuthenticator authenticator;
	/** The agent node bean. */
	private DirectoryAgentNodeBean agentNodeBean;
	/** The memory. */
	private Memory memory;
	/** The execution cycle. */
	private SimpleExecutionCycle executionCycle;
	/** The communication bean. */
	private CommunicationBean communicationBean;
	/** The autowired annotation bean post processor. */
	private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
	/** The required annotation bean post processor. */
	private RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor;
	/** The common annotation bean post processor. */
	private CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;
	/** The osgi service factory bean. */
	private OsgiServiceFactoryBean osgiServiceFactoryBean;
	/** The child of bean factory post processor. */
	private ChildOfBeanFactoryPostProcessor childOfBeanFactoryPostProcessor;
	/** The logging bean factory post processor. */
	private LoggingBeanFactoryPostProcessor loggingBeanFactoryPostProcessor;
	/** The destroyable. */
	private Destroyable destroyable;
}
