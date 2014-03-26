package okeanos.data.internal.services;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Assert;
import org.junit.Test;

public class UUIDGeneratorImplTest {

	@Test
	public void testGenerateUUID() {
		UUIDGeneratorImpl uuidGenerator = new UUIDGeneratorImpl();

		String uuid = uuidGenerator.generateUUID();

		// assuming a sha 256 uuid generator
		Assert.assertThat(uuid.getBytes().length, is(equalTo(64)));
	}

}
