package okeanos.control.entities;

import java.util.List;
import java.util.Set;

import javax.measure.quantity.Power;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jscience.physics.amount.Amount;

import com.google.common.collect.Range;

import de.dailab.jiactng.agentcore.knowledge.IFact;

/**
 * Represents a proposed run, i.e., a run that would be possible for the
 * household device. A dishwasher could, for example, start anytime after its
 * {@code earliestStartTime}, e.g. 1:30, however, needs the given time slots
 * {@code neededSlots} to finish. Also, it needs to finish before its
 * {@code latestEndTime}, e.g. 3:30. For one run, it is only possible to either
 * produce, or consume energy. If both is possible for a device, multiple runs
 * have to be defined.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface PossibleRun extends IFact {

	/**
	 * Gets the earliest start time.
	 * 
	 * @return the earliest start time
	 */
	DateTime getEarliestStartTime();

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the latest end time.
	 * 
	 * @return the latest end time
	 */
	DateTime getLatestEndTime();

	/**
	 * Returns the needed length of the run.
	 * 
	 * @return the length of the run
	 */
	Period getLengthOfRun();

	LoadFlexiblity getLoadFlexibilityOfRun();

	void setLoadFlexibilityOfRun(LoadFlexiblity loadFlexibility);

	/**
	 * Specifies a sequence of values a device needs to adhere to. That is, a
	 * dishwasher could need more energy while heating up and less while
	 * actually washing the dishes. There is no choice for the optimization
	 * algorithm which value to choose from, all values are strictly specified.
	 * A device can either specify a continuous range through
	 * {@link #getRangeOfPossibleLoads()}, a set of specific values through
	 * {@link #getPossibleLoads()} or the fixed values through
	 * {@link #getNeededSlots()} from which the optimization algorithm can
	 * choose from.
	 * 
	 * @return a list of the needed slots
	 * 
	 * @see #getPossibleLoads()
	 * @see #getRangeOfPossibleLoads()
	 */
	List<Slot> getNeededSlots();

	/**
	 * Specifies the possibilities the optimization algorithm can choose a value
	 * out of. It can choose any value from the set for the whole run. A device
	 * can either specify a continuous range through
	 * {@link #getRangeOfPossibleLoads()}, a set of specific values through
	 * {@link #getPossibleLoads()} or the fixed values through
	 * {@link #getNeededSlots()} from which the optimization algorithm can
	 * choose from.
	 * 
	 * @return a set of possible loads for the run
	 * 
	 * @see #getNeededSlots()
	 * @see #getRangeOfPossibleLoads()
	 */
	Set<Amount<Power>> getPossibleLoads();

	/**
	 * Specifies the upper and lower boundary if the device supports the
	 * assignment of arbitrary values between two points.<br>
	 * A device can either specify a continuous range through
	 * {@link #getRangeOfPossibleLoads()}, a set of specific values through
	 * {@link #getPossibleLoads()} or the fixed values through
	 * {@link #getNeededSlots()} from which the optimization algorithm can
	 * choose from. When a range is given, the optimization algorithm can choose
	 * any value between the upper and lower boundary for the whole run.
	 * However, there can only be one and the same value for the whole run.
	 * 
	 * @return the range containing the upper and lower boundary
	 * 
	 * @see #getNeededSlots()
	 * @see #getPossibleLoads()
	 */
	Range<Amount<Power>> getRangeOfPossibleLoads();

	/**
	 * Sets the earliest start time.
	 * 
	 * @param earliestStartTime
	 *            the new earliest start time
	 */
	void setEarliestStartTime(DateTime earliestStartTime);

	/**
	 * Sets the latest end time.
	 * 
	 * @param latestEndTime
	 *            the new latest end time
	 */
	void setLatestEndTime(DateTime latestEndTime);

	/**
	 * Sets the length of run.
	 * 
	 * @param lengthOfRun
	 *            the new length of run
	 */
	void setLengthOfRun(Period lengthOfRun);

	/**
	 * Sets the needed slots.
	 * 
	 * @param neededSlots
	 *            the new needed slots
	 */
	void setNeededSlots(List<Slot> neededSlots);

	/**
	 * Sets the possible loads.
	 * 
	 * @param possibleLoads
	 *            the new possible loads
	 */
	void setPossibleLoads(Set<Amount<Power>> possibleLoads);

	/**
	 * Sets the range of possible loads.
	 * 
	 * @param rangeOfPossibleLoads
	 *            the new range of possible loads
	 */
	void setRangeOfPossibleLoads(Range<Amount<Power>> rangeOfPossibleLoads);

}
