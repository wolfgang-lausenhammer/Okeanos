package okeanos.math.regression;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class LargeSerializableConcurrentSkipListMap<K, V> extends
		ConcurrentSkipListMap<K, V> {
	private static final long serialVersionUID = -1525292268623732635L;

	private void writeObject(ObjectOutputStream stream) throws IOException {
		for (Map.Entry<K, V> e : this.entrySet()) {
			stream.writeObject(e.getKey());
			stream.writeObject(e.getValue());
		}
		// We do it like this because items can be added and removed
		// during serialization and an "end of object" indicator is more
		// robust given this
		stream.writeObject(new EndObject());
		stream.writeObject(new EndObject());
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		while (true) {
			Object k = stream.readObject();
			Object v = stream.readObject();
			if ((k instanceof EndObject) && (v instanceof EndObject)) {
				break;
			}
			this.put((K) k, (V) v);
		}
	}

	private static class EndObject implements Serializable {
		private static final long serialVersionUID = -5347717251973679220L;
	}
}
