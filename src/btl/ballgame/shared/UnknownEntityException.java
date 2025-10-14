package btl.ballgame.shared;

public class UnknownEntityException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnknownEntityException(int id) {
		super("Unsupported entity id: " + id);
	}
}
