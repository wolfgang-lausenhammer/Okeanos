package okeanos.control.entities.utilities;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.measure.quantity.Power;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.math.regression.LargeSerializableConcurrentSkipListMap;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;

/**
 * Provides utility methods that work with {@link Schedule} instances.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ScheduleUtil implements Comparator<Schedule> {

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/**
	 * Instantiates a new schedule util.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 */
	public ScheduleUtil(final ControlEntitiesProvider controlEntitiesProvider) {
		this.controlEntitiesProvider = controlEntitiesProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final Schedule schedule1, final Schedule schedule2) {
		if (schedule1 == null || schedule2 == null) {
			return -1;
		}

		if (schedule1.getSchedule().size() != schedule2.getSchedule().size()) {
			return -1;
		}

		for (Entry<DateTime, Slot> entry : schedule1.getSchedule().entrySet()) {
			DateTime key = entry.getKey();
			Slot value = entry.getValue();
			if (!schedule2.getSchedule().containsKey(key)) {
				return -1;
			}
			if (!schedule2.getSchedule().get(key).getLoad()
					.approximates(value.getLoad())) {
				return -1;
			}
		}

		Amount<Power> sumSchedule1 = Amount.valueOf(0, Power.UNIT);
		Amount<Power> sumSchedule2 = Amount.valueOf(0, Power.UNIT);

		for (Slot value : schedule1.getSchedule().values()) {
			sumSchedule1.plus(value.getLoad());
		}
		for (Slot value : schedule2.getSchedule().values()) {
			sumSchedule2.plus(value.getLoad());
		}

		return sumSchedule1.compareTo(sumSchedule2);
	}

	/**
	 * Calculates the difference between two schedule instances.
	 * {@code schedule1 - schedule2}.
	 * 
	 * @param schedule1
	 *            the schedule1
	 * @param schedule2
	 *            the schedule2
	 * @return the difference
	 */
	public Schedule minus(final Schedule schedule1, final Schedule schedule2) {
		Schedule difference = controlEntitiesProvider.getNewSchedule();

		LargeSerializableConcurrentSkipListMap<DateTime, Slot> schedule = new LargeSerializableConcurrentSkipListMap<>();
		schedule.putAll(schedule1.getSchedule());
		for (Entry<DateTime, Slot> entry : schedule2.getSchedule().entrySet()) {
			DateTime key = entry.getKey();
			Slot value = entry.getValue();

			Slot newSlot = controlEntitiesProvider.getNewSlot();
			if (schedule.containsKey(key)) {
				newSlot.setLoad(schedule.get(key).getLoad()
						.minus(value.getLoad()));
			} else {
				newSlot.setLoad(value.getLoad().times(-1));
			}
			schedule.put(key, newSlot);
		}

		difference.setSchedule(schedule);
		return difference;
	}

	/**
	 * Calculates the sum of two schedule instances.
	 * {@code schedule1 + schedule2}.
	 * 
	 * @param schedule1
	 *            the schedule1
	 * @param schedule2
	 *            the schedule2
	 * @return the sum
	 */
	public Schedule plus(final Schedule schedule1, final Schedule schedule2) {
		Schedule sum = controlEntitiesProvider.getNewSchedule();

		LargeSerializableConcurrentSkipListMap<DateTime, Slot> schedule = new LargeSerializableConcurrentSkipListMap<>();
		schedule.putAll(schedule1.getSchedule());
		for (Entry<DateTime, Slot> entry : schedule2.getSchedule().entrySet()) {
			DateTime key = entry.getKey();
			Slot value = entry.getValue();

			Slot newSlot = controlEntitiesProvider.getNewSlot();
			if (schedule.containsKey(key)) {
				newSlot.setLoad(schedule.get(key).getLoad()
						.plus(value.getLoad()));
			} else {
				newSlot.setLoad(value.getLoad());
			}
			schedule.put(key, newSlot);
		}

		sum.setSchedule(schedule);
		return sum;
	}

	/**
	 * To schedule.
	 * 
	 * @param optimizedRuns
	 *            the optimized runs
	 * @return the schedule
	 */
	public Schedule toSchedule(final List<OptimizedRun> optimizedRuns) {
		Schedule schedule = controlEntitiesProvider.getNewSchedule();

		LargeSerializableConcurrentSkipListMap<DateTime, Slot> scheduleMap = new LargeSerializableConcurrentSkipListMap<>();
		for (OptimizedRun run : optimizedRuns) {
			DateTime currentEntryTime = run.getStartTime();

			for (Slot slot : run.getNeededSlots()) {
				Slot newSlot = controlEntitiesProvider.getNewSlot();
				if (scheduleMap.containsKey(currentEntryTime)) {
					newSlot.setLoad(scheduleMap.get(currentEntryTime).getLoad()
							.plus(slot.getLoad()));
				} else {
					newSlot.setLoad(slot.getLoad());
				}

				scheduleMap.put(currentEntryTime, newSlot);
				currentEntryTime = currentEntryTime.plusMinutes(15);
			}
		}

		schedule.setSchedule(scheduleMap);
		return schedule;
	}
}
