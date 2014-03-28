package okeanos.data.internal.services;

import javax.inject.Inject;

import okeanos.data.services.PricingService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class PricingServiceImplSpringTest {
	@Inject
	private PricingService pricingService;

	@Test
	public void testRefreshPricingResource() {
		System.out.println("abc");
		System.out.println(pricingService);
		System.out.println("abc");
	}

}
