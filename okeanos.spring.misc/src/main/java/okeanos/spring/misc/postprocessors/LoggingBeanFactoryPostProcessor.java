package okeanos.spring.misc.postprocessors;

/**
 * Auto injects the underlying implementation of logger into the bean with field
 * having annotation <code>Logger</code>.
 *
 */
import java.lang.reflect.Field;

import okeanos.spring.misc.stereotypes.Logging;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

@Component
public class LoggingBeanFactoryPostProcessor implements BeanPostProcessor {

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(final Object bean,
			String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				// make the field accessible if defined private
				ReflectionUtils.makeAccessible(field);
				if (field.getAnnotation(Logging.class) != null) {
					org.slf4j.Logger log = LoggerFactory.getLogger(bean
							.getClass());
					field.set(bean, log);
				}
			}
		});
		return bean;
	}
}