package okeanos.data.internal;

import javax.management.NotificationBroadcasterSupport;
import javax.management.openmbean.OpenDataException;
import javax.security.auth.Destroyable;

import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

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

	Destroyable d;
	
	ReflectUtils reflectUtils;
	MethodProxy methodProxy;
	DisposableBean disposableBean;
	FastClass fastClass;
	OpenDataException openDataException;
	IEffector iEffector;
	ActionScope actionScope;
	
	Agent agent;
	SimpleAgentNode simpleAgentNode;
}
