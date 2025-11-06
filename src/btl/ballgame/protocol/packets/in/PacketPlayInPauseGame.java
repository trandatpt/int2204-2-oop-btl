package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInPauseGame extends NetworkPacket implements IPacketPlayIn {
	public PacketPlayInPauseGame() {}; /* for packet decoding */
	
	private boolean paused;
	public PacketPlayInPauseGame(boolean pause) {
		this.paused = pause;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBool(this.paused);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.paused = buffer.readBool();
	}
}
