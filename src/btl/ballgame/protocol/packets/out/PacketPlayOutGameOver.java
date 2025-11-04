package btl.ballgame.protocol.packets.out;

import java.util.ArrayList;
import java.util.List;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.GameOverType;

public class PacketPlayOutGameOver extends NetworkPacket implements IPacketPlayOut {
	private boolean classic;
	
	// for VERSUS mode
	private GameOverType type;
	private List<String> participants;
	private int redScore;
	private int blueScore;
	private long time;
	
	// for CLASSIC mode
	private int score;
	private int level;
	private int highScore;
	
	public PacketPlayOutGameOver() {}
	
	// constructor for VERSUS
	public PacketPlayOutGameOver(
		GameOverType type, 
		List<String> participants, 
		int redScore, int blueScore,
		long time
	) {
		this.classic = false;
		this.type = type;
		this.participants = participants;
		this.redScore = redScore;
		this.blueScore = blueScore;
		this.time = time;
	}
	
	// constructor for CLASSIC
	public PacketPlayOutGameOver(int score, int level, int highScore) {
		this.classic = true;
		this.score = score;
		this.level = level;
		this.highScore = highScore;
	}

	public boolean isClassic() {
		return classic;
	}

	public GameOverType getType() {
		return type;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public int getRedScore() {
		return redScore;
	}

	public int getBlueScore() {
		return blueScore;
	}

	public long getTime() {
		return time;
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}

	public int getHighScore() {
		return highScore;
	}
	
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBool(classic);
		
		if (!classic) {
			buf.writeInt8((byte) type.ordinal());
			buf.writeInt8((byte) participants.size());
			for (String name : participants) {
				buf.writeU8String(name);
			}
			buf.writeInt32(redScore);
			buf.writeInt32(blueScore);
			buf.writeInt64(time);
		} else {
			buf.writeInt32(score);
			buf.writeInt32(level);
			buf.writeInt32(highScore);
		}
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.classic = buf.readBool();
		
		if (!classic) {
			this.type = GameOverType.values()[buf.readInt8()];
			int count = buf.readInt8();
			this.participants = new ArrayList<>(count);
			for (int i = 0; i < count; i++) {
				this.participants.add(buf.readU8String());
			}
			this.redScore = buf.readInt32();
			this.blueScore = buf.readInt32();
			this.time = buf.readInt64();
		} else {
			this.score = buf.readInt32();
			this.level = buf.readInt32();
			this.highScore = buf.readInt32();
		}
	}
}
