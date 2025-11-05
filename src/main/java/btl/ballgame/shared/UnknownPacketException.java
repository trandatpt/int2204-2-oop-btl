package btl.ballgame.shared;

public class UnknownPacketException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnknownPacketException(int id) {
		super("Unsupported packet header type: 0x" + Integer.toHexString(id).toUpperCase());
	}
}
