package btl.ballgame.protocol.packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class NetworkPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public void write(ObjectOutputStream out) {
		try {
			out.writeObject(this);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write packet", e);
		}
	}
	
	public static NetworkPacket readNextPacket(ObjectInputStream in) {
		try {
			return (NetworkPacket) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Failed to read next packet", e);
		}
	}
}
