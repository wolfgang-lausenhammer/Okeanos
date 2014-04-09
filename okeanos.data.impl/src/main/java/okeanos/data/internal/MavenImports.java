package okeanos.data.internal;

import javax.management.NotificationBroadcasterSupport;
import javax.management.openmbean.OpenDataException;
import javax.security.auth.Destroyable;

import okeanos.spring.misc.postprocessors.LoggingBeanFactoryPostProcessor;

import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.quartz.ObjectAlreadyExistsException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;
import org.springframework.transaction.TransactionException;

import de.dailab.jiactng.agentcore.Agent;
import de.dailab.jiactng.agentcore.SimpleAgentNode;
import de.dailab.jiactng.agentcore.action.scope.ActionScope;
import de.dailab.jiactng.agentcore.comm.CommunicationBean;
import de.dailab.jiactng.agentcore.comm.broker.ActiveMQBroker;
import de.dailab.jiactng.agentcore.comm.transport.jms.JMSMessageTransport;
import de.dailab.jiactng.agentcore.conf.GenericAgentProperties;
import de.dailab.jiactng.agentcore.directory.DirectoryAgentNodeBean;
import de.dailab.jiactng.agentcore.environment.IEffector;
import de.dailab.jiactng.agentcore.execution.SimpleExecutionCycle;
import de.dailab.jiactng.agentcore.knowledge.Memory;
import de.dailab.jiactng.agentcore.management.jmx.JaasAuthenticator;
import de.dailab.jiactng.agentcore.ontology.ThisAgentDescription;

/**
 * The Class MavenImports.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The action scope. */
	private ActionScope actionScope;
	/** The agent. */
	private Agent agent;
	/** The agent node bean. */
	private DirectoryAgentNodeBean agentNodeBean;
	/** The async annotation bean post processor. */
	private AsyncAnnotationBeanPostProcessor asyncAnnotationBeanPostProcessor;
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
	/** The disposable bean. */
	private DisposableBean disposableBean;
	/** The execution cycle. */
	private SimpleExecutionCycle executionCycle;
	/** The fast class. */
	private FastClass fastClass;
	/** The i effector. */
	private IEffector iEffector;
	/** The logging bean factory post processor. */
	private LoggingBeanFactoryPostProcessor loggingBeanFactoryPostProcessor;
	/** The memory. */
	private Memory memory;
	/** The method proxy. */
	private MethodProxy methodProxy;
	/** The object already exists exception. */
	private ObjectAlreadyExistsException objectAlreadyExistsException;
	/** The open data exception. */
	private OpenDataException openDataException;
	/** The osgi service factory bean. */
	private OsgiServiceFactoryBean osgiServiceFactoryBean;
	/** The prop. */
	private GenericAgentProperties prop;
	/** The reflect utils. */
	private ReflectUtils reflectUtils;
	/** The required annotation bean post processor. */
	private RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor;
	/** The simple agent node. */
	private SimpleAgentNode simpleAgentNode;
	/** The task executor factory bean. */
	private TaskExecutorFactoryBean taskExecutorFactoryBean;
	/** The this agent description. */
	private ThisAgentDescription thisAgentDescription;
	/** The thread pool task scheduler. */
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	/** The transaction exception. */
	private TransactionException transactionException;
	/** The transport. */
	private JMSMessageTransport transport;
	/** The x. */
	private NotificationBroadcasterSupport x;
}
