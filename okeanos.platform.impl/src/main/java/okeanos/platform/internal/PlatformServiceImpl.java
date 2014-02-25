package okeanos.platform.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okeanos.platform.PlatformService;

import org.springframework.stereotype.Component;

/**
 * Internal implementation of our example OSGi service
 */
@Component
public final class PlatformServiceImpl implements PlatformService {
	public String scramble(String text) {
		List charList = new ArrayList();

		char[] textChars = text.toCharArray();
		for (int i = 0; i < textChars.length; i++) {
			charList.add(new Character(textChars[i]));
		}

		Collections.shuffle(charList);

		char[] mixedChars = new char[text.length()];
		for (int i = 0; i < mixedChars.length; i++) {
			mixedChars[i] = ((Character) charList.get(i)).charValue();
		}

		return new String(mixedChars);
	}
}
