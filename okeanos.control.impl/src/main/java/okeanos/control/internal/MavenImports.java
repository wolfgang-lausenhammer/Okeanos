package okeanos.control.internal;

import javax.management.NotificationBroadcasterSupport;

import okeanos.data.services.UUIDGenerator;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;

import de.dailab.jiactng.agentcore.lifecycle.AbstractLifecycle;

/**
 * The Class MavenImports.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The abstract lifecycle. */
	private AbstractLifecycle abstractLifecycle;
	/** The async annotation bean post processor. */
	private AsyncAnnotationBeanPostProcessor asyncAnnotationBeanPostProcessor;
	/** The disposable bean. */
	private DisposableBean disposableBean;
	/** The fast class. */
	private FastClass fastClass;
	/** The method proxy. */
	private MethodProxy methodProxy;
	/** The notification broadcaster support. */
	private NotificationBroadcasterSupport notificationBroadcasterSupport;
	/** The reflect utils. */
	private ReflectUtils reflectUtils;
	/** The task executor factory bean. */
	private TaskExecutorFactoryBean taskExecutorFactoryBean;
	/** The thread pool task scheduler. */
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;
}
