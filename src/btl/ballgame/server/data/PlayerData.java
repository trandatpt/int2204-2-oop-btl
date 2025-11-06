package btl.ballgame.server.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import btl.ballgame.server.ArkanoidServer;
import btl.ballgame.shared.libs.Utils;
import btl.ballgame.shared.libs.external.Json;

public class PlayerData {
	public static final String PASSWORD_HASH = "password-sha256", NAME = "display-name",
	HIGHSCORE_SOLO = "solo-hiscore", TOTAL_WINS = "total-wins";
	
	private UUID uuid;
	private Json data;
	private File file;
	
	private PlayerData(File file, UUID playerId) throws Exception {
		this.uuid = playerId;
		this.file = file;
		this.data = Json.read(Files.readString(file.toPath()));
		this.data.set("uuid", uuid.toString());
	}
	
	public String getName() {
		return !data.has(NAME) ? "Error" : data.at(NAME).asString();
	}
	 
	public String getPasswordHash() {
		return !data.has(PASSWORD_HASH) ? null : data.at(PASSWORD_HASH).asString();
	}
	
	public int getSoloHighScore() {
		return !data.has(HIGHSCORE_SOLO) ? 0 : data.at(HIGHSCORE_SOLO).asInteger();
	}
	
	public void setHighScore(int score) {
		data.set(HIGHSCORE_SOLO, score);
		save();
	}
	
	public int getTotalWins() {
		return !data.has(TOTAL_WINS) ? 0 : data.at(TOTAL_WINS).asInteger();
	}
	
	public void addTotalWins(int moreWins) {
		data.set(TOTAL_WINS, getTotalWins() + 1);
		save();
	}
	
	public void setPasswordHash(String hash) {
		data.set(PASSWORD_HASH, hash);
		save();
	}
	
	public void save() {
		try {
			Files.writeString(file.toPath(), this.data.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static PlayerData get(UUID uuid) {
		var dataManager = ArkanoidServer.getServer().getDataManager();
		var file = dataManager.getUserDataFile(uuid);
		
		if (!file.exists()) {
			return null;
		}
		
		try {
			return new PlayerData(file, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			file.renameTo(new File(uuid.toString() + ".json.corrupted"));
		}
		return null;
	}
	
	public static PlayerData create(UUID uuid, String name, String password) {
		var dataManager = ArkanoidServer.getServer().getDataManager();
		var file = dataManager.getUserDataFile(uuid);
		if (file.exists()) {
			// prevent overwriting existing data
			return get(uuid);
		}
		try {
			var json = Json.object();
			json.set("uuid", uuid.toString());
			json.set(NAME, name);
			json.set(PASSWORD_HASH, Utils.SHA256(password));
			Files.writeString(file.toPath(), json.toString());
			return new PlayerData(file, uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
