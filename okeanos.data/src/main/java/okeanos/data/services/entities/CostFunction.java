package okeanos.data.services.entities;

import org.joda.time.DateTime;

public interface CostFunction {
	Price getPrice();

	DateTime getValidFromDateTime();

	DateTime getValidThroughDateTime();
}
