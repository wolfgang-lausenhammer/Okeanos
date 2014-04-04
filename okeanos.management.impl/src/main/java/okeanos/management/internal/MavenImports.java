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

public class MavenImports {
	NotificationBroadcasterSupport x;
	GenericAgentProperties prop;
	ActiveMQBroker broker;
	JMSMessageTransport transport;
	JaasAuthenticator authenticator;
	DirectoryAgentNodeBean agentNodeBean;
	Memory memory;
	SimpleExecutionCycle executionCycle;
	CommunicationBean communicationBean;

	AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
	RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor;
	CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;
	OsgiServiceFactoryBean osgiServiceFactoryBean;
	ChildOfBeanFactoryPostProcessor childOfBeanFactoryPostProcessor;
	LoggingBeanFactoryPostProcessor loggingBeanFactoryPostProcessor;

	Destroyable d;
}
