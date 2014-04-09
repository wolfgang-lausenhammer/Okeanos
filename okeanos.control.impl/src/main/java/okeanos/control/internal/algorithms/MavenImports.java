package okeanos.control.internal.algorithms;

import javax.management.NotificationBroadcasterSupport;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;

import de.dailab.jiactng.agentcore.lifecycle.AbstractLifecycle;
import okeanos.data.services.UUIDGenerator;

/**
 * The Class MavenImports.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The disposable bean. */
	private DisposableBean disposableBean;
	/** The fast class. */
	private FastClass fastClass;
	/** The method proxy. */
	private MethodProxy methodProxy;
	/** The reflect utils. */
	private ReflectUtils reflectUtils;
	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;
	/** The abstract lifecycle. */
	private AbstractLifecycle abstractLifecycle;
	private NotificationBroadcasterSupport notificationBroadcasterSupport;
	private AsyncAnnotationBeanPostProcessor asyncAnnotationBeanPostProcessor;
	private TaskExecutorFactoryBean taskExecutorFactoryBean;
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
}
