package okeanos.control.entities.utilities;

import java.io.IOException;
import java.io.OutputStream;
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
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Provides utility methods that work with {@link Schedule} instances.
 * 
 * @author Wolfgang Lausenhammer
 */
public class ScheduleUtil implements Comparator<Schedule> {

	/** The control entities provider. */
	private ControlEntitiesProvider controlEntitiesProvider;

	/** The pricing service. */
	private PricingService pricingService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory
			.getLogger(ScheduleUtil.class);

	/**
	 * Instantiates a new schedule util.
	 * 
	 * @param controlEntitiesProvider
	 *            the control entities provider
	 * @param pricingService
	 *            the pricing service
	 */
	public ScheduleUtil(final ControlEntitiesProvider controlEntitiesProvider,
			PricingService pricingService) {
		this.controlEntitiesProvider = controlEntitiesProvider;
		this.pricingService = pricingService;
	}

	/**
	 * Compare by costs.
	 * 
	 * @param schedule1
	 *            the schedule1
	 * @param schedule2
	 *            the schedule2
	 * @return the int
	 */
	public int compareByCosts(final Schedule schedule1, final Schedule schedule2) {
		if (schedule1 == null || schedule2 == null) {
			return -1;
		}

		if (schedule1.getSchedule() == null || schedule2.getSchedule() == null) {
			return -1;
		}

		double costsSchedule1 = 0;
		double costsSchedule2 = 0;

		for (Entry<DateTime, Slot> entry : schedule1.getSchedule().entrySet()) {
			CostFunction costFunction = pricingService.getCostFunction(entry
					.getKey());

			if (costFunction == null) {
				costsSchedule1 += 0;
			} else {
				costsSchedule1 += costFunction.getPrice().getCostAtConsumption(
						entry.getValue().getLoad());
			}
		}

		for (Entry<DateTime, Slot> entry : schedule2.getSchedule().entrySet()) {
			CostFunction costFunction = pricingService.getCostFunction(entry
					.getKey());

			if (costFunction == null) {
				costsSchedule2 += 0;
			} else {
				costsSchedule2 += costFunction.getPrice().getCostAtConsumption(
						entry.getValue().getLoad());
			}
		}

		return Double.compare(costsSchedule1, costsSchedule2);
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
		List<OptimizedRun> optimizedRunsAdapted = optimizedRuns;
		if (optimizedRunsAdapted == null) {
			optimizedRunsAdapted = new LinkedList<>();
		}
		Schedule schedule = controlEntitiesProvider.getNewSchedule();

		Map<DateTime, Slot> scheduleMap = new ConcurrentSkipListMap<>();
		for (OptimizedRun run : optimizedRunsAdapted) {
			DateTime currentEntryTime = run.getStartTime();

			// set all slots to 0
			DateTime currentTime = currentEntryTime.withTimeAtStartOfDay();
			DateTime endOfCurrentEntryTime = currentTime.withTime(23, 45, 0, 0);
			while (currentTime.isBefore(endOfCurrentEntryTime)
					|| currentTime.isEqual(endOfCurrentEntryTime)) {
				if (!scheduleMap.containsKey(currentTime)) {
					Slot newSlot = controlEntitiesProvider.getNewSlot();
					newSlot.setLoad(Amount.valueOf(0, Power.UNIT));
					scheduleMap.put(currentTime, newSlot);
				}

				currentTime = currentTime.plusMinutes(Constants.SLOT_INTERVAL);
			}

			for (Slot slot : run.getNeededSlots()) {
				Slot newSlot = controlEntitiesProvider.getNewSlot();
				if (scheduleMap.containsKey(currentEntryTime)) {
					newSlot.setLoad(scheduleMap.get(currentEntryTime).getLoad()
							.plus(slot.getLoad()));
				} else {
					newSlot.setLoad(slot.getLoad());
				}

				scheduleMap.put(currentEntryTime, newSlot);
				currentEntryTime = currentEntryTime
						.plusMinutes(Constants.SLOT_INTERVAL);
			}
		}

		schedule.setSchedule(scheduleMap);
		return schedule;
	}

	/**
	 * Write schedule to stream.
	 * 
	 * @param schedulesPerDay
	 *            the schedules per day
	 * @param os
	 *            the OutputStream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeScheduleToStream(final Schedule schedulesPerDay,
			final OutputStream os) throws IOException {
		writeScheduleToStream(schedulesPerDay,
				new ConcurrentSkipListMap<String, Schedule>(),
				new ConcurrentSkipListMap<DateTime, Price>(), os);
	}

	/**
	 * Write a schedule (usually the total schedule) to a stream and the
	 * schedule of certain devices too.
	 * 
	 * @param schedule
	 *            the schedules per day
	 * @param deviceSchedules
	 *            the device schedules per day
	 * @param prices
	 *            the prices
	 * @param os
	 *            the OutputStream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void writeScheduleToStream(final Schedule schedule,
			final Map<String, Schedule> deviceSchedules,
			final Map<DateTime, Price> prices, final OutputStream os)
			throws IOException {
		List<String> deviceNames = new LinkedList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("Date\tTotal Consumption [W]");
		sb.append("\tTotal Price [$]\tPrice per kWh [$]");

		if (deviceSchedules != null && !deviceSchedules.isEmpty()) {
			for (String deviceName : deviceSchedules.keySet()) {
				sb.append(String.format("\t%s", deviceName));
				deviceNames.add(deviceName);
			}
		}
		sb.append("\n");

		for (DateTime time : schedule.getSchedule().keySet()) {
			sb.append(String.format("%s", time.toString("yyyy-MM-dd HH:mm")));

			Amount<Power> totalConsumption = null;
			if (deviceSchedules != null && !deviceSchedules.isEmpty()) {
				totalConsumption = Amount.valueOf(0, Power.UNIT);
				for (Schedule deviceSchedule : deviceSchedules.values()) {
					if (deviceSchedule.getSchedule().containsKey(time)) {
						totalConsumption = totalConsumption.plus(deviceSchedule
								.getSchedule().get(time).getLoad());
					}
				}
			} else {
				totalConsumption = schedule.getSchedule().get(time).getLoad();
			}

			sb.append(String.format("\t%.1f",
					totalConsumption.doubleValue(Power.UNIT)));

			if (prices != null && prices.get(time) != null) {
				sb.append(String.format(
						"\t%.5f",
						prices.get(time).getCostAtConsumption(
								schedule.getSchedule().get(time).getLoad())));

				sb.append(String.format("\t%.5f", prices.get(time)
						.getCostAtConsumption(Amount.valueOf(1000, Power.UNIT))));
			} else {
				sb.append("\t0\t0");
			}

			for (String currentDevice : deviceNames) {
				Schedule scheduleOfDevice = deviceSchedules.get(currentDevice);
				if (scheduleOfDevice.getSchedule().isEmpty()) {
					sb.append("\t0");
				} else {
					sb.append(String.format("\t%.1f",
							scheduleOfDevice.getSchedule().get(time).getLoad()
									.doubleValue(Power.UNIT)));
				}
			}

			sb.append("\n");
		}

		os.write(sb.toString().getBytes("UTF-8"));
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
		if (finalSchedule == null) {
			LOG.error("final schedule is NULL 1!!!! ({})", finalSchedule);
		}

		for (Schedule currentSchedule : schedule) {
			finalSchedule = plus(finalSchedule, currentSchedule);
		}
		if (finalSchedule == null) {
			LOG.error("final schedule is NULL 2!!!! ({})", finalSchedule);
		}

		return finalSchedule;
	}
}
