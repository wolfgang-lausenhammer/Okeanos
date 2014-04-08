package okeanos.control.entities.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The Class SlotImplTest.
 * 
 * @author Wolfgang Lausenhammer
 */
@RunWith(Parameterized.class)
public class SlotImplTest {

	/** The Constant THOUSAND. */
	private static final long THOUSAND = 1000;

	/**
	 * Data.
	 * 
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "my-slot-id-1", Amount.valueOf(0, Power.UNIT) },
				{ "my-slot-id-2", Amount.valueOf(1, Power.UNIT) },
				{ "my-slot-id-3", Amount.valueOf(-THOUSAND, Power.UNIT) },
				{ "my-slot-id-4", Amount.valueOf(THOUSAND, Power.UNIT) },
				{ "my-slot-id-5", null }, { null, null }, });
	}

	/** The id. */
	private String id;

	/** The load. */
	private Amount<Power> load;

	/** The slot. */
	private SlotImpl slot;

	/**
	 * Instantiates a new slot impl test.
	 * 
	 * @param id
	 *            the id
	 * @param load
	 *            the load
	 */
	public SlotImplTest(final String id, final Amount<Power> load) {
		this.id = id;
		this.load = load;
	}

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.slot = new SlotImpl(id);
	}

	/**
	 * Test get id.
	 */
	@Test
	public void testGetId() {
		String id = slot.getId();

		assertThat(id, is(equalTo(this.id)));
	}

	/**
	 * Test load.
	 */
	@Test
	public void testLoad() {
		slot.setLoad(load);

		Amount<Power> load = slot.getLoad();

		assertThat(load, is(equalTo(this.load)));
	}

}
