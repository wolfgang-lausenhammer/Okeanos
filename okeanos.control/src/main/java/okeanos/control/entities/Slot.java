package okeanos.control.entities;

/**
 * Represents one time slot within a run. Every time slot can have different
 * consumption/production, e.g., a dishwasher could need power to heat up the
 * water, but then not needs as much power anymore for washing the dishes.
 * 
 * @author Wolfgang Lausenhammer
 */
public interface Slot {
	String getId();

	double getLoad();

	void setLoad(double load);
}
