package btl.ballgame.protocol;

import java.util.HashMap;
import java.util.Map;

import btl.ballgame.protocol.packets.ConnectionCtx;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;

public class PacketRegistry {
	private Map<Class<? extends NetworkPacket>, 
		PacketHandler<? extends NetworkPacket, ? extends ConnectionCtx>> 
	registered = new HashMap<>(); 
	
	public <T extends NetworkPacket, U extends ConnectionCtx>
	void register(
		Class<? extends NetworkPacket> packetIdentifier, PacketHandler<T, U> handler
	) {
		registered.put(packetIdentifier, handler);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NetworkPacket, U extends ConnectionCtx>
	PacketHandler<T, U> getHandle(Class<? extends NetworkPacket> packet) {
		return (PacketHandler<T, U>) this.registered.get(packet);
	}
}
