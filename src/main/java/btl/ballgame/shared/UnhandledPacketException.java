package btl.ballgame.shared;

public class UnhandledPacketException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public <T> UnhandledPacketException(Class<T> clazz) {
		super("Packet with class " + clazz + " has no registered handler!");
	}
}
