package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.IWorld;

public class PacketPlayOutInitMatch extends NetworkPacket implements IPacketPlayOut {
	private int worldWidth, worldHeight;
	
	public PacketPlayOutInitMatch() {}
	
	public PacketPlayOutInitMatch(IWorld world) {
		this(world.getWidth(), world.getHeight());
	}
	
	public PacketPlayOutInitMatch(int ww, int wh) {
		this.worldWidth = ww;
		this.worldHeight = wh;
	}
	
	public int getWorldHeight() {
		return worldHeight;
	}
	
	public int getWorldWidth() {
		return worldWidth;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(this.worldWidth);
		buffer.writeInt32(this.worldHeight);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.worldWidth = buffer.readInt32();
		this.worldHeight = buffer.readInt32();
	}
 }
