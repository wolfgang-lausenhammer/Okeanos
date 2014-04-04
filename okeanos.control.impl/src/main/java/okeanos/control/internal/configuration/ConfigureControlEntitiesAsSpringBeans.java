package okeanos.control.internal.configuration;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to let Spring know about all the control entities.
 */
@Configuration
public class ConfigureControlEntitiesAsSpringBeans {

	/**
	 * Configuration.
	 * 
	 * @param id
	 *            the id
	 * @return the configuration
	 */
	@Bean
	public okeanos.control.entities.Configuration configuration(
			@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		return new ConfigurationImpl(id);
	}

	/**
	 * Optimized run.
	 * 
	 * @param id
	 *            the id
	 * @return the optimized run
	 */
	@Bean
	public OptimizedRun optimizedRun(
			@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		return new OptimizedRunImpl(id);
	}

	/**
	 * Possible run.
	 * 
	 * @param id
	 *            the id
	 * @return the possible run
	 */
	@Bean
	public PossibleRun possibleRun(
			@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		return new PossibleRunImpl(id);
	}

	/**
	 * Schedule.
	 * 
	 * @param id
	 *            the id
	 * @return the schedule
	 */
	@Bean
	public Schedule schedule(
			@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		return new ScheduleImpl(id);
	}

	/**
	 * Slot.
	 * 
	 * @param id
	 *            the id
	 * @return the slot
	 */
	@Bean
	public Slot slot(@Value("#{ uuidGenerator.generateUUID() }") final String id) {
		return new SlotImpl(id);
	}
}
