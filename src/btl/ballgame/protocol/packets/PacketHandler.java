package btl.ballgame.protocol.packets;

import btl.ballgame.protocol.ConnectionCtx;

/**
 * <h1>PacketHandler — Runtime Dispatcher for PennyWort Protocol Packets</h1>
 *
 * <p>{@code PacketHandler} is the functional interface responsible for handling
 * decoded {@link NetworkPacket} instances after they have been received and
 * deserialized by the <b>PennyWort Protocol (PWP)</b>.</p>
 * 
 * <h2>Generic Type Parameters</h2>
 * <ul>
 *   <li><b>T</b> – The specific {@link NetworkPacket} type this handler processes.</li>
 *   <li><b>U</b> – The type of {@link ConnectionCtx} providing connection-specific context (for the server-side, its {@link PlayerConnection}).</li>
 * </ul>
 */
public interface PacketHandler<T extends NetworkPacket, U extends ConnectionCtx> {
	/**
	 * Handles the given decoded packet using the provided connection context.
	 * <p>
	 * This method is invoked once per packet immediately after deserialization.
	 * Implementations should perform all necessary validation, game logic, and any
	 * response sending here.
	 *
	 * @param packet  the decoded packet instance to process
	 * @param context the connection/session context associated with this packet
	 */
	void handle(T packet, U context);
}
