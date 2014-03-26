package okeanos.data.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import okeanos.data.services.entities.CostFunction;

import org.joda.time.DateTime;

import com.google.gson.JsonSyntaxException;

public interface PricingService {
	Collection<CostFunction> getCostFunctions();

	Collection<CostFunction> getCostFunctions(DateTime to);

	Collection<CostFunction> getCostFunctions(DateTime from, DateTime to);

	CostFunction getCostFunction(DateTime at);

	void refreshPricingResource() throws JsonSyntaxException, IOException;
}
