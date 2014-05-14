package okeanos.data.internal.services;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;

import okeanos.data.services.entities.CostFunction;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The Class PricingServiceImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
public class PricingServiceImplTest {

	/** The Constant FIFTEEN_O_CLOCK. */
	private static final DateTime FIFTEEN_O_CLOCK = DateTime
			.parse("2014-03-20T15:00:00Z");

	/** The Constant TEN_O_CLOCK. */
	private static final DateTime TEN_O_CLOCK = DateTime
			.parse("2014-03-20T10:00:00Z");

	/** The Constant TWENTY_O_CLOCK. */
	private static final DateTime TWENTY_O_CLOCK = DateTime
			.parse("2014-03-20T20:00:00Z");

	/** The pricing resource. */
	private Resource pricingResource;

	/** The pricing service. */
	private PricingServiceImpl pricingService;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pricingResource = new ClassPathResource("test-prices.json");
		pricingService = new PricingServiceImpl(pricingResource);
	}

	/**
	 * Test get cost functions.
	 */
	@Test
	public void testGetCostFunctions() {
		Collection<CostFunction> costFunctions = pricingService
				.getCostFunctions();

		Iterator<CostFunction> iterator = costFunctions.iterator();
		CostFunction func1 = iterator.next();
		CostFunction func2 = iterator.next();
		CostFunction func3 = iterator.next();
		assertThat(iterator.hasNext(), is(equalTo(false)));
		assertThat(func1.getValidFromDateTime(), is(equalTo(TEN_O_CLOCK)));
		assertThat(func2.getValidFromDateTime(), is(equalTo(FIFTEEN_O_CLOCK)));
		assertThat(func3.getValidFromDateTime(), is(equalTo(TWENTY_O_CLOCK)));
	}

	/**
	 * Test get cost functions date time from to.
	 */
	@Test
	public void testGetCostFunctionsDateTimeFromTo() {
		DateTime from = DateTime.parse("2014-03-20T17:00:00Z");
		DateTime to = DateTime.parse("2014-03-20T19:00:00Z");

		Collection<CostFunction> costFunctions = pricingService
				.getCostFunctions(from, to);

		Iterator<CostFunction> iterator = costFunctions.iterator();
		CostFunction func1 = iterator.next();
		assertThat(iterator.hasNext(), is(equalTo(false)));
		assertThat(func1.getValidFromDateTime(), is(equalTo(FIFTEEN_O_CLOCK)));
	}

	/**
	 * Test get cost functions date time to.
	 */
	@Test
	public void testGetCostFunctionsDateTimeTo() {
		DateTimeUtils.setCurrentMillisFixed(DateTime.parse(
				"2014-01-01T00:00:00Z").getMillis());
		DateTime to = DateTime.parse("2014-03-20T19:00:00Z");

		Collection<CostFunction> costFunctions = pricingService
				.getCostFunctions(to);

		Iterator<CostFunction> iterator = costFunctions.iterator();
		CostFunction func1 = iterator.next();
		CostFunction func2 = iterator.next();
		assertThat(iterator.hasNext(), is(equalTo(false)));
		assertThat(func1.getValidFromDateTime(), is(equalTo(TEN_O_CLOCK)));
		assertThat(func2.getValidFromDateTime(), is(equalTo(FIFTEEN_O_CLOCK)));
	}

	/**
	 * Test get cost function somewhere between.
	 */
	@Test
	public void testGetCostFunctionSomewhereBetween() {
		DateTime at = DateTime.parse("2014-03-20T19:00:00Z");

		CostFunction costFunction = pricingService.getCostFunction(at);

		assertThat(costFunction, is(notNullValue()));
		assertThat(costFunction.getValidFromDateTime(), is(FIFTEEN_O_CLOCK));
	}

	/**
	 * Test get cost function ten.
	 */
	@Test
	public void testGetCostFunctionTen() {
		DateTime at = TEN_O_CLOCK;

		CostFunction costFunction = pricingService.getCostFunction(at);

		assertThat(costFunction, is(notNullValue()));
		assertThat(costFunction.getValidFromDateTime(), is(TEN_O_CLOCK));
	}

	/**
	 * Test get cost function too early.
	 */
	@Test
	public void testGetCostFunctionTooEarly() {
		DateTime at = DateTime.parse("2014-01-01T00:00:00Z");

		CostFunction costFunction = pricingService.getCostFunction(at);

		assertThat(costFunction, is(nullValue()));
	}

	/**
	 * Test get cost function too late.
	 */
	@Test
	public void testGetCostFunctionTooLate() {
		DateTime at = DateTime.parse("2015-01-01T00:00:00Z");

		CostFunction costFunction = pricingService.getCostFunction(at);

		assertThat(costFunction, is(nullValue()));
	}
}
