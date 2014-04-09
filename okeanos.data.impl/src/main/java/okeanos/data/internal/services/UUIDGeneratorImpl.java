package okeanos.data.internal.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import okeanos.data.services.UUIDGenerator;

import org.springframework.stereotype.Component;

/**
 * Implements several features for generating UUIDs. At the moment uses
 * {@link UUID#randomUUID()} for the UUID and {@code SHA-256} for the hashing
 * function.
 * 
 * @author Wolfgang Lausenhammer
 */
@Component("uuidGenerator")
public class UUIDGeneratorImpl implements UUIDGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see okeanos.data.services.UUIDGenerator#generateUUID()
	 */
	@Override
	public String generateUUID() {
		String uuidBasic = UUID.randomUUID().toString();

		// generate SHA-256
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(uuidBasic.getBytes());
			byte[] byteData = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available on the system", e);
		}
	}
}
