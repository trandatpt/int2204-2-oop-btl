package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.ArkanoidMode;

public class PacketPlayInCreateRoom extends NetworkPacket implements IPacketPlayIn {
	public boolean isPrivate;
	public String name;
	// raw settings data
	public ArkanoidMode gamemode;
	public short firstToScore;
	public short timePerRound;
	public short teamLives;
	
	public PacketPlayInCreateRoom() {}
	
	public PacketPlayInCreateRoom(
		String name, boolean isPrivate, 
		ArkanoidMode gamemode, int firstToScore,
		int timePerRound, int teamLives
	) {
		this.name = name;
		this.isPrivate = isPrivate;
		this.gamemode = gamemode;
		this.firstToScore = (short) firstToScore;
		this.timePerRound = (short) timePerRound;
		this.teamLives = (short) teamLives;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBool(isPrivate);
		buffer.writeU8String(name);
		buffer.writeInt8((byte) gamemode.ordinal());
		buffer.writeInt16(firstToScore);
		buffer.writeInt16(timePerRound);
		buffer.writeInt16(teamLives);
	}
	
	@Override
	public void read(PacketByteBuf buffer) {
		this.isPrivate = buffer.readBool();
		this.name = buffer.readU8String();
		this.gamemode = ArkanoidMode.of(buffer.readInt8());
		this.firstToScore = buffer.readInt16();
		this.timePerRound = buffer.readInt16();
		this.teamLives = buffer.readInt16();
	}
}
