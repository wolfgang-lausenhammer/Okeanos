package okeanos.data.internal.services;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Assert;
import org.junit.Test;

/**
 * The Class UUIDGeneratorImplTest.
 */
public class UUIDGeneratorImplTest {

	/** The Constant SHA_256_BYTES. */
	private static final int SHA_256_BYTES = 64;

	/**
	 * Test generate uuid.
	 */
	@Test
	public void testGenerateUUID() {
		UUIDGeneratorImpl uuidGenerator = new UUIDGeneratorImpl();

		String uuid = uuidGenerator.generateUUID();

		// assuming a sha 256 uuid generator
		Assert.assertThat(uuid.getBytes().length, is(equalTo(SHA_256_BYTES)));
	}

}
