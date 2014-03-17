package okeanos.data.internal.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import okeanos.data.services.UUIDGenerator;

import org.springframework.stereotype.Component;

@Component("uuidGeneratorImpl")
public class UUIDGeneratorImpl implements UUIDGenerator {
	@Override
	public String generateUUID() {
		String uuidBasic = UUID.randomUUID().toString();

		// generate SHA-256
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(uuidBasic.getBytes());
			byte byteData[] = md.digest();
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
