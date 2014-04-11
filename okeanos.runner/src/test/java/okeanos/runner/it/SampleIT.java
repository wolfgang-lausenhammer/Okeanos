package okeanos.runner.it;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

// TODO: Auto-generated Javadoc
/**
 * The Class SampleIT.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SampleIT {

	/** The context. */
	@Inject
	private BundleContext context;

	/**
	 * Config.
	 * 
	 * @return the option[]
	 */
	@Configuration
	public Option[] config() {
		return options(junitBundles());
	}

	/**
	 * Gets the hello service.
	 * 
	 * @return the hello service
	 */
	@Test
	public void getHelloService() {
		assertNotNull(context);
		// assertEquals("Hello Pax!", context.toString());
	}
}
