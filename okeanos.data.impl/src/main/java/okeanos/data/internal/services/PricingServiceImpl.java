package okeanos.data.internal.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.inject.Inject;

import okeanos.data.internal.services.pricing.entities.serialization.CostFunctionDeserializer;
import okeanos.data.internal.services.pricing.entities.serialization.ListCostFunctionDeserializer;
import okeanos.data.internal.services.pricing.entities.serialization.PriceDeserializer;
import okeanos.data.services.PricingService;
import okeanos.data.services.TimeService;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * This implementation of the {@link PricingService} reads data points from a
 * json file. Data points need to contain the {@link DateTime} at which they are
 * valid and the costs for a certain amount of energy.
 * 
 * @author Wolfgang Lausenhammer
 * 
 */
@Component
public class PricingServiceImpl implements PricingService {
	private static final Logger log = LoggerFactory
			.getLogger(PricingServiceImpl.class);

	private TimeService timeService;

	private Resource pricingResource;

	private Gson gson;

	private Map<DateTime, CostFunction> costFunctions;

	@Inject
	public PricingServiceImpl(
			TimeService timeService,
			@Value("${okeanos.pricing.service.pathToPricingFile}") Resource pricingResource)
			throws JsonSyntaxException, IOException {
		this.timeService = timeService;
		this.pricingResource = pricingResource;

		registerGson();
		updatePricingResource(pricingResource);
	}

	private void registerGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(new TypeToken<List<CostFunction>>() {
		}.getType(), new ListCostFunctionDeserializer());
		builder.registerTypeAdapter(CostFunction.class,
				new CostFunctionDeserializer());
		builder.registerTypeAdapter(Price.class, new PriceDeserializer());
		gson = builder.create();
	}

	@Override
	public void refreshPricingResource() throws JsonSyntaxException,
			IOException {
		String jsonString = IOUtils.toString(pricingResource.getInputStream());
		log.trace("New pricing resource with content: {}", jsonString);

		List<CostFunction> costFunctionsList = gson.fromJson(jsonString,
				new TypeToken<List<CostFunction>>() {
				}.getType());

		Map<DateTime, CostFunction> costFunctionsMap = new ConcurrentSkipListMap<>();
		for (CostFunction func : costFunctionsList) {
			costFunctionsMap.put(func.getValidFromDateTime(), func);
		}

		this.costFunctions = costFunctionsMap;

		log.trace("New cost functions:\n[{}]",
				StringUtils.join(costFunctionsList, StringUtils.LF));
	}

	public void updatePricingResource(Resource pricingResource)
			throws JsonSyntaxException, IOException {
		log.trace("Pricing resource updated to {}", pricingResource);
		this.pricingResource = pricingResource;
		refreshPricingResource();
	}

	@Override
	public Collection<CostFunction> getCostFunctions() {
		return Collections.unmodifiableCollection(costFunctions.values());
	}

	@Override
	public Collection<CostFunction> getCostFunctions(DateTime to) {
		return getCostFunctions(DateTime.now(), to);
	}

	@Override
	public Collection<CostFunction> getCostFunctions(DateTime from, DateTime to) {
		if (from.isAfter(to)) {
			throw new RuntimeException(String.format(
					"from [%s] must be earlier than to [%s]", from, to));
		}

		List<CostFunction> matchingCostFunctions = new LinkedList<>();
		for (Entry<DateTime, CostFunction> entry : costFunctions.entrySet()) {
			DateTime key = entry.getKey();
			CostFunction value = entry.getValue();
			// either validFrom is after the from
			// or validThrough is long enough to cover from as well
			if (key.isEqual(from)
					|| (key.isAfter(from) || value.getValidThroughDateTime()
							.isAfter(from)) && key.isBefore(to)) {
				matchingCostFunctions.add(entry.getValue());
			}
		}

		return Collections.unmodifiableList(matchingCostFunctions);
	}

	@Override
	public CostFunction getCostFunction(DateTime at) {
		CostFunction closestMatchingCostFunction = null;

		for (Entry<DateTime, CostFunction> entry : costFunctions.entrySet()) {
			DateTime key = entry.getKey();
			CostFunction value = entry.getValue();
			// either validFrom is after the from
			// or validThrough is long enough to cover from as well
			if (key.isEqual(at) || key.isBefore(at)
					&& value.getValidThroughDateTime().isAfter(at)) {
				closestMatchingCostFunction = entry.getValue();
			} else if (closestMatchingCostFunction != null) {
				break;
			}
		}

		return closestMatchingCostFunction;
	}
}
