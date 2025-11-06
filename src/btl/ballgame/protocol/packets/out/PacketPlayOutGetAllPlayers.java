package btl.ballgame.protocol.packets.out;

import java.util.ArrayList;
import java.util.List;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutGetAllPlayers extends NetworkPacket implements IPacketPlayOut {
	private List<PlayerDetails> players;
	
	public PacketPlayOutGetAllPlayers() {}
	
	public PacketPlayOutGetAllPlayers(List<PlayerDetails> players) {
		this.players = players;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		// write the number of players first
		buffer.writeVarUInt(players.size());
		for (PlayerDetails player : players) {
			buffer.writeU8String(player.username);
			buffer.writeVarUInt(player.mpWins);
			buffer.writeVarUInt(player.classicHiScore);
		}
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		int size = buffer.readVarUInt();
		this.players = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			PlayerDetails info = new PlayerDetails(
				buffer.readU8String(),
				buffer.readVarUInt(),
				buffer.readVarUInt()
			);
			players.add(info);
		}
	}

	public List<PlayerDetails> getPlayerDetails() {
		return players;
	}

	public static record PlayerDetails(String username, int mpWins, int classicHiScore) {};
}
