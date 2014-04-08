package okeanos.control.internal.algorithms;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.reflect.FastClass;

import okeanos.data.services.UUIDGenerator;

/**
 * The Class MavenImports.
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
}
