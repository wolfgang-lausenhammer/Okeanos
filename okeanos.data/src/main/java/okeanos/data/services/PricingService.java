package okeanos.data.services;

import java.io.IOException;
import java.util.Collection;

import okeanos.data.services.entities.CostFunction;

import org.joda.time.DateTime;

/**
 * An interface for all issues related to pricing. Every energy provider could
 * have its own pricing scheme. Agents can then choose whichever energy provider
 * they get their energy from.
 * 
 * Functions aim at providing requesters with a detailed overview of the prices.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PricingService {

	/**
	 * Returns the cost function at a specific point in time. Gets the most
	 * up-to-date cost function. If no valid cost function could be found for
	 * the point in time, returns null.
	 * 
	 * @param at
	 *            the point in time at which to fetch the cost function
	 * @return the cost function
	 */
	CostFunction getCostFunction(DateTime at);

	/**
	 * Returns a list of all cost functions for all available time slots.
	 * 
	 * @return the cost functions
	 */
	Collection<CostFunction> getCostFunctions();

	/**
	 * Returns a list of all cost functions for the time slots until the given
	 * {@link DateTime}. {@code to} is exclusive and a cost function starting
	 * its validity at that time will not be returned.
	 * 
	 * @param to
	 *            the point in time to fetch the cost functions to
	 * @return the cost functions to {@code to}
	 */
	Collection<CostFunction> getCostFunctions(DateTime to);

	/**
	 * Returns a list of all cost functions starting with time slots
	 * {@code from} until {@code to}. {@code from} is inclusive, {@code to} is
	 * exclusive.
	 * 
	 * @param from
	 *            the point in time to start fetching the cost functions
	 * @param to
	 *            the point in time to fetch the cost functions to
	 * @return the cost functions from {@code from} to {@code to}
	 */
	Collection<CostFunction> getCostFunctions(DateTime from, DateTime to);

	/**
	 * Refreshes the pricing resource.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred, if the resource
	 *             could not be read.
	 */
	void refreshPricingResource() throws IOException;
}
