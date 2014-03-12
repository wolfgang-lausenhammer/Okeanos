package okeanos.runner.it;

import static org.ops4j.pax.exam.CoreOptions.options;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

public class ServerConfiguration {

	@Configuration
	public Option[] configuration() {
		return options();
	}
}
