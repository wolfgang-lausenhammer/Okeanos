package okeanos.model.internal;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;

import okeanos.data.services.UUIDGenerator;

/**
 * The Class MavenImports.
 * 
 * @author Wolfgang Lausenhammer
 */
@SuppressWarnings("unused")
public class MavenImports {
	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;
	/** The disposable bean. */
	private DisposableBean disposableBean;
	/** The reflect utils. */
	private ReflectUtils reflectUtils;
	/** The method proxy. */
	private MethodProxy methodProxy;
	/** The fast class. */
	private FastClass fastClass;
}
