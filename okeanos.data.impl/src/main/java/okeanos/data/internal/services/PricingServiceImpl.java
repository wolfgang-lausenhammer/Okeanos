package okeanos.data.internal.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.inject.Inject;

import okeanos.data.internal.services.pricing.entities.serialization.CostFunctionDeserializer;
import okeanos.data.internal.services.pricing.entities.serialization.ListCostFunctionDeserializer;
import okeanos.data.internal.services.pricing.entities.serialization.PriceDeserializer;
import okeanos.data.services.Constants;
import okeanos.data.services.PricingService;
import okeanos.data.services.entities.CostFunction;
import okeanos.data.services.entities.Price;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * This implementation of the {@link PricingService} reads data points from a
 * json file. Data points need to contain the {@link DateTime} at which they are
 * valid, how long they are valid and the costs for a certain amount of energy.
 * 
 * @author Wolfgang Lausenhammer
 * 
 */
@Component("pricingService")
public class PricingServiceImpl implements PricingService {

	/** The cost functions. */
	private Map<DateTime, CostFunction> costFunctions;

	/** Gson (de)serializer. */
	private Gson gson;

	/** The Logger. */
	private static final Logger LOG = LoggerFactory
			.getLogger(PricingServiceImpl.class);

	/** The pricing resource to fetch the prices from. */
	private Resource pricingResource;

	/**
	 * Instantiates a new pricing service implementation.
	 * 
	 * @param pricingResource
	 *            the pricing resource
	 * @throws IOException
	 *             Signals that an I/O exception has occurred, if the resource
	 *             could not be read.
	 */
	@Inject
	public PricingServiceImpl(
			@Value("${okeanos.pricing.service.pathToPricingFile}") final Resource pricingResource)
			throws IOException {
		this.pricingResource = pricingResource;

		registerGson();
		updatePricingResource(pricingResource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.PricingService#getCostFunction(org.joda.time.DateTime
	 * )
	 */
	@Override
	public CostFunction getCostFunction(final DateTime at) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.PricingService#getCostFunctions()
	 */
	@Override
	public Collection<CostFunction> getCostFunctions() {
		return Collections.unmodifiableCollection(costFunctions.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.PricingService#getCostFunctions(org.joda.time.DateTime
	 * )
	 */
	@Override
	public Collection<CostFunction> getCostFunctions(final DateTime to) {
		return getCostFunctions(DateTime.now(), to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * okeanos.data.services.PricingService#getCostFunctions(org.joda.time.DateTime
	 * , org.joda.time.DateTime)
	 */
	@Override
	public Collection<CostFunction> getCostFunctions(final DateTime from,
			final DateTime to) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.PricingService#refreshPricingResource()
	 */
	@Override
	@Scheduled(fixedRate = Constants.PRICING_SERVICE_RESOURCE_REFRESH_RATE_MS)
	public void refreshPricingResource() throws IOException {
		String jsonString = IOUtils.toString(pricingResource.getInputStream());
		LOG.trace("New pricing resource with content: {}", jsonString);
		LOG.debug("{}: refreshing....", DateTime.now(DateTimeZone.UTC));

		List<CostFunction> costFunctionsList = gson.fromJson(jsonString,
				new TypeToken<List<CostFunction>>() {
				}.getType());

		Map<DateTime, CostFunction> costFunctionsMap = new ConcurrentSkipListMap<>();
		for (CostFunction func : costFunctionsList) {
			costFunctionsMap.put(func.getValidFromDateTime(), func);
		}

		this.costFunctions = costFunctionsMap;

		LOG.trace("New cost functions:\n[{}]",
				StringUtils.join(costFunctionsList, StringUtils.LF));
	}

	/**
	 * Register gson.
	 */
	private void registerGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(new TypeToken<List<CostFunction>>() {
		}.getType(), new ListCostFunctionDeserializer());
		builder.registerTypeAdapter(CostFunction.class,
				new CostFunctionDeserializer());
		builder.registerTypeAdapter(Price.class, new PriceDeserializer());
		gson = builder.create();
	}

	/**
	 * Update pricing resource.
	 * 
	 * @param pricingResource
	 *            the pricing resource
	 * @throws JsonSyntaxException
	 *             the json syntax exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void updatePricingResource(final Resource pricingResource)
			throws JsonSyntaxException, IOException {
		LOG.trace("Pricing resource updated to {}", pricingResource);
		this.pricingResource = pricingResource;
		refreshPricingResource();
	}
}
