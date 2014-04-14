package okeanos.control.entities.utilities;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.measure.quantity.Power;

import okeanos.control.entities.OptimizedRun;
import okeanos.control.entities.Schedule;
import okeanos.control.entities.Slot;
import okeanos.control.entities.provider.ControlEntitiesProvider;
import okeanos.data.services.Constants;

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

		if (schedule1.getSchedule() == null || schedule2.getSchedule() == null) {
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
			sumSchedule1 = sumSchedule1.plus(value.getLoad());
		}
		for (Slot value : schedule2.getSchedule().values()) {
			sumSchedule2 = sumSchedule2.plus(value.getLoad());
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

		Map<DateTime, Slot> schedule = new ConcurrentSkipListMap<>();
		schedule.putAll(schedule1.getSchedule());
		for (Entry<DateTime, Slot> entry : schedule2.getSchedule().entrySet()) {
			DateTime key = entry.getKey();
			Slot value = entry.getValue();

			Slot newSlot = controlEntitiesProvider.getNewSlot();
			if (schedule.containsKey(key)) {
				newSlot.setLoad(schedule.get(key).getLoad()
						.minus(value.getLoad()));
			} else {
				newSlot.setLoad(value.getLoad());
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
		Map<DateTime, Slot> scheduleMap1 = schedule1.getSchedule();
		Map<DateTime, Slot> scheduleMap2 = schedule2.getSchedule();

		if (scheduleMap1 == null) {
			scheduleMap1 = new ConcurrentSkipListMap<>();
		}
		if (scheduleMap2 == null) {
			scheduleMap2 = new ConcurrentSkipListMap<>();
		}

		Map<DateTime, Slot> schedule = new ConcurrentSkipListMap<>();
		schedule.putAll(scheduleMap1);

		for (Entry<DateTime, Slot> entry : scheduleMap2.entrySet()) {
			DateTime key = entry.getKey();
			Slot value = entry.getValue();

			Slot newSlot = controlEntitiesProvider.getNewSlot();
			if (schedule.containsKey(key)) {
				newSlot.setLoad(schedule.get(key).getLoad()
						.plus(value.getLoad()));
			} else {
				newSlot.setLoad(value.getLoad().plus(
						Amount.valueOf(0, Power.UNIT)));
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
		List<OptimizedRun> optimizedRunsAdapted = optimizedRuns;
		if (optimizedRunsAdapted == null) {
			optimizedRunsAdapted = new LinkedList<>();
		}
		Schedule schedule = controlEntitiesProvider.getNewSchedule();

		Map<DateTime, Slot> scheduleMap = new ConcurrentSkipListMap<>();
		for (OptimizedRun run : optimizedRunsAdapted) {
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
				currentEntryTime = currentEntryTime.plusMinutes(Constants.SLOT_INTERVAL);
			}
		}

		schedule.setSchedule(scheduleMap);
		return schedule;
	}

	/**
	 * Calculates the sum of two or more schedule instances.
	 * {@code schedule1 + schedule2 + schedule 3 + ...}.
	 * 
	 * @param schedule
	 *            the schedules
	 * @return the sum
	 */
	public Schedule sum(final Schedule... schedule) {
		Schedule finalSchedule = controlEntitiesProvider.getNewSchedule();

		for (Schedule currentSchedule : schedule) {
			finalSchedule = plus(finalSchedule, currentSchedule);
		}

		return finalSchedule;
	}
}
