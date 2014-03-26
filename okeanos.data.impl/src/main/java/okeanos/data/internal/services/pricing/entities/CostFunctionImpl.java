package okeanos.data.internal.services.pricing.entities;

import org.joda.time.DateTime;

import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

public class CostFunctionImpl implements CostFunction {
	private Price pricesFromDateTime;
	private DateTime validFromDateTime;
	private DateTime validThroughDateTime;

	public CostFunctionImpl(DateTime validAtDateTime,
			DateTime validThroughDateTime, Price pricesAtDateTime) {
		this.validFromDateTime = validAtDateTime;
		this.validThroughDateTime = validThroughDateTime;
		this.pricesFromDateTime = pricesAtDateTime;
	}

	@Override
	public Price getPrice() {
		return pricesFromDateTime;
	}

	@Override
	public DateTime getValidFromDateTime() {
		return validFromDateTime;
	}

	@Override
	public DateTime getValidThroughDateTime() {
		return validThroughDateTime;
	}

	@Override
	public String toString() {
		return String
				.format("CostFunctionImpl [validFromDateTime=%s, validThroughDateTime=%s, pricesFromDateTime=%s]",
						validFromDateTime, validThroughDateTime,
						pricesFromDateTime);
	}

}
