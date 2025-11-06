package btl.ballgame.protocol.packets.out;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class PacketPlayOutMatchJoin extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutMatchJoin() {}
	
	private ArkanoidMode arkanoidMode;
	private UUID matchId;
	private Map<UUID, String> nameMap;
	private TeamColor teamColor;
	
	public PacketPlayOutMatchJoin(
		UUID matchId, ArkanoidMode mode,
		Map<UUID, String> nameMap,
		TeamColor teamColor
	) {
		this.matchId = matchId;
		this.nameMap = nameMap;
		this.arkanoidMode = mode;
		this.teamColor = teamColor;
	}
	
	public UUID getMatchId() {
		return matchId;
	}
	
	public Map<UUID, String> getNameMap() {
		return nameMap;
	}
	
	public ArkanoidMode getArkanoidMode() {
		return arkanoidMode;
	}
	
	public TeamColor getTeamColor() {
		return teamColor;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt8((byte) arkanoidMode.ordinal());
		buf.writeInt64(matchId.getMostSignificantBits());
		buf.writeInt64(matchId.getLeastSignificantBits());
		buf.writeInt8((byte) teamColor.ordinal());
		buf.writeInt8((byte) nameMap.size());
		nameMap.forEach((uuid, name) -> {
			buf.writeInt64(uuid.getMostSignificantBits());
			buf.writeInt64(uuid.getLeastSignificantBits());
			buf.writeU8String(name);
		});
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.arkanoidMode = ArkanoidMode.of(buf.readInt8());
		this.matchId = new UUID(buf.readInt64(), buf.readInt64()); // MSB, LSB
		this.teamColor = TeamColor.of(buf.readInt8());
		int size = buf.readInt8(); 
		this.nameMap = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			UUID playerUuid = new UUID(buf.readInt64(), buf.readInt64());
			this.nameMap.put(playerUuid, buf.readU8String());
		}
	}
 }
