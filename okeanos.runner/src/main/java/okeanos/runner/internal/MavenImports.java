package okeanos.runner.internal;

import javax.management.NotificationBroadcasterSupport;
import javax.security.auth.Destroyable;

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
	DirectoryAgentNodeBean agentNodeBean;
	JaasAuthenticator authenticator;
	AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
	ActiveMQBroker broker;
	CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;
	CommunicationBean communicationBean;
	Destroyable d;
	SimpleExecutionCycle executionCycle;
	Memory memory;

	OsgiServiceFactoryBean osgiServiceFactoryBean;
	GenericAgentProperties prop;
	RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor;
	JMSMessageTransport transport;

	NotificationBroadcasterSupport x;
}
