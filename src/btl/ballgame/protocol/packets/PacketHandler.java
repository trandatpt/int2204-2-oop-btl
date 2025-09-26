package btl.ballgame.protocol.packets;

public interface PacketHandler<T extends NetworkPacket, U extends ConnectionCtx> {
	void handle(T packet, U context);
}
