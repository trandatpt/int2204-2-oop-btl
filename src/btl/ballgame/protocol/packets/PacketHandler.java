package btl.ballgame.protocol.packets;

import btl.ballgame.protocol.ConnectionCtx;

public interface PacketHandler<T extends NetworkPacket, U extends ConnectionCtx> {
	void handle(T packet, U context);
}
