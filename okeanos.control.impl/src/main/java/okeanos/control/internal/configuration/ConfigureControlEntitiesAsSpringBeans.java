package okeanos.control.internal.configuration;

import javax.inject.Inject;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.PossibleRun;
import okeanos.control.entities.PossibleRunsConfiguration;
import okeanos.control.entities.RunConstraint;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.impl.ConfigurationImpl;
import okeanos.control.entities.impl.OptimizedRunImpl;
import okeanos.control.entities.impl.PossibleRunImpl;
import okeanos.control.entities.impl.PossibleRunsConfigurationImpl;
import okeanos.control.entities.impl.RunConstraintImpl;
import okeanos.control.entities.impl.ScheduleImpl;
import okeanos.control.entities.impl.SlotImpl;
import okeanos.data.services.UUIDGenerator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class to let Spring know about all the control entities.
 * 
 * @author Wolfgang Lausenhammer
 */
@Configuration
public class ConfigureControlEntitiesAsSpringBeans {

	/** The uuid generator. */
	private UUIDGenerator uuidGenerator;

	/**
	 * Configuration.
	 * 
	 * @return the configuration
	 */
	@Bean
	@Scope("prototype")
	public okeanos.control.entities.Configuration configuration() {
		return new ConfigurationImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Optimized run.
	 * 
	 * @return the optimized run
	 */
	@Bean
	@Scope("prototype")
	public OptimizedRun optimizedRun() {
		return new OptimizedRunImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Possible run.
	 * 
	 * @return the possible run
	 */
	@Bean
	@Scope("prototype")
	public PossibleRun possibleRun() {
		return new PossibleRunImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Possible runs configuration.
	 * 
	 * @return the possible runs configuration
	 */
	@Bean
	@Scope("prototype")
	public PossibleRunsConfiguration possibleRunsConfiguration() {
		return new PossibleRunsConfigurationImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Run constraint.
	 * 
	 * @return the run constraint
	 */
	@Bean
	@Scope("prototype")
	public RunConstraint runConstraint() {
		return new RunConstraintImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Schedule.
	 * 
	 * @return the schedule
	 */
	@Bean
	@Scope("prototype")
	public Schedule schedule() {
		return new ScheduleImpl(uuidGenerator.generateUUID());
	}

	/**
	 * Sets the uuid generator.
	 * 
	 * @param uuidGenerator
	 *            the new uuid generator
	 */
	@Inject
	public void setUuidGenerator(final UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	/**
	 * Slot.
	 * 
	 * @return the slot
	 */
	@Bean
	@Scope("prototype")
	public Slot slot() {
		return new SlotImpl(uuidGenerator.generateUUID());
	}
}
