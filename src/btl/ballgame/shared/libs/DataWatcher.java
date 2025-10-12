package btl.ballgame.shared.libs;

import btl.ballgame.protocol.PacketByteBuf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * DataWatcher is a utility class for tracking key-value pairs of
 * an Entity or others, suitable for use on both the client and the server
 */
public class DataWatcher {
	private final Map<Integer, DataWatcherEntry> entries;

	public DataWatcher() {
		this.entries = new HashMap<>();
	}

	/**
	 * Adds or updates a watched key with the specified value. The type of the value
	 * is automatically detected based on its Java class.
	 *
	 * @param keyId the identifier for the key
	 * @param value the value to watch
	 * @throws IllegalArgumentException if the value type is not supported
	 */
	public void watch(int keyId, Object value) {
		byte typeId = detectType(value);
		if (typeId == -1) {
			throw new IllegalArgumentException("Unsupported type: " + value.getClass());
		}
		entries.put(keyId, new DataWatcherEntry(keyId, typeId, value));
	}
	
	/**
	 * Removes a watched key from this DataWatcher.
	 *
	 * @param keyId the identifier of the key to remove
	 */
	public void unwatch(int keyId) {
		entries.remove(keyId);
	}
	
	/**
	 * Retrieves the value associated with a watched key.
	 *
	 * @param keyId the identifier of the key
	 * @return the value of the key, or null if the key is not watched
	 */
	public Object get(int keyId) {
		DataWatcherEntry entry = entries.get(keyId);
		return entry != null ? entry.value : null;
	}
	
	/**
	 * Unwatches every key.
	 */
	public void clear() {
		this.entries.clear();
	}

	/**
	 * Writes all entries to the buffer
	 * Format: [size][key id 1][type id 1][value 1]...
	 */
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(entries.size());
		for (DataWatcherEntry entry : entries.values()) {
			entry.write(buffer);
		}
	}

	/**
	 * Reads entries from the buffer
	 */
	public void read(PacketByteBuf buffer) {
		entries.clear();
		int len = buffer.readInt32();
		for (int i = 0; i < len; ++i) {
			DataWatcherEntry dw = DataWatcherEntry.read(buffer);
			entries.put(dw.keyId, dw);
		}
	}

	/**
	 * Returns all entries
	 */
	public Collection<DataWatcherEntry> entries() {
		return entries.values();
	}
	
	/**
	 * Infers type ID from Java object.
	 */
	private byte detectType(Object value) {
		if (value instanceof Byte)
			return 0;
		if (value instanceof Short)
			return 1;
		if (value instanceof Integer)
			return 2;
		if (value instanceof Float)
			return 3;
		if (value instanceof String)
			return 4;
		return -1;
	}
}
