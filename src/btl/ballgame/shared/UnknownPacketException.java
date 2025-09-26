package btl.ballgame.shared;

public class UnknownPacketException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnknownPacketException(Class<?> packet) {
		super("Unsupported packet header type: " + packet.toString());
	}
}
