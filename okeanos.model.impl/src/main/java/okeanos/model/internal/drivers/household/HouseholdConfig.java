package okeanos.model.internal.drivers.household;

import java.io.IOException;

import javax.inject.Inject;
import javax.measure.quantity.Power;

import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.UUIDGenerator;
import okeanos.model.entities.Load;

import org.jscience.physics.amount.Amount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * The Class HouseholdConfig.
 */
@Configuration
public class HouseholdConfig {

	/** The Constant HOUSEHOLD_10_KWH. */
	private static final Amount<Power> HOUSEHOLD_10_KWH = Amount.valueOf(10,
			Power.UNIT);

	/** The Constant HOUSEHOLD_25_KWH. */
	private static final Amount<Power> HOUSEHOLD_25_KWH = Amount.valueOf(25,
			Power.UNIT);

	/** The Constant HOUSEHOLD_28_KWH. */
	private static final Amount<Power> HOUSEHOLD_28_KWH = Amount.valueOf(28,
			Power.UNIT);

	/** The Constant HOUSEHOLD_30_KWH. */
	private static final Amount<Power> HOUSEHOLD_30_KWH = Amount.valueOf(30,
			Power.UNIT);

	/** The Constant HOUSEHOLD_33_KWH. */
	private static final Amount<Power> HOUSEHOLD_33_KWH = Amount.valueOf(33,
			Power.UNIT);

	/** The Constant HOUSEHOLD_35_KWH. */
	private static final Amount<Power> HOUSEHOLD_35_KWH = Amount.valueOf(35,
			Power.UNIT);

	/** The Constant HOUSEHOLD_50_KWH. */
	private static final Amount<Power> HOUSEHOLD_50_KWH = Amount.valueOf(10,
			Power.UNIT);

	/** The control entities provider. */
	@Inject
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The resource. */
	@Inject
	@Value("${okeanos.model.internal.drivers.household.Household_30kWh_per_day_H0_Load_Profile.loadProfilePath}")
	private Resource resource;

	/** The uuid generator. */
	@Inject
	private UUIDGenerator uuidGenerator;

	/**
	 * Household10k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household10kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_10_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household25k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household25kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_25_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household28k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household28kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_28_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household30k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household30kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_30_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household33k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household33kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_33_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household35k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household35kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_35_KWH,
				controlEntitiesProvider);
	}

	/**
	 * Household50k wh.
	 * 
	 * @return the load
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Bean
	public Load household50kWh() throws IOException {
		return new Household_H0_Load_Profile(resource,
				uuidGenerator.generateUUID(), HOUSEHOLD_50_KWH,
				controlEntitiesProvider);
	}
}
