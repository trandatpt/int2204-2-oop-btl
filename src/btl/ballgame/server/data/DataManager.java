package btl.ballgame.server.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import btl.ballgame.protocol.packets.out.PacketPlayOutGetAllPlayers.PlayerDetails;
import btl.ballgame.shared.libs.external.Json;

public class DataManager {
	private File fileRoot;
	private File userDataRoot;
	private File spFile;
	
	public DataManager() {
		this.fileRoot = new File("data");
		this.userDataRoot = new File(this.fileRoot, "users");
		if (!this.userDataRoot.exists()) {
			this.userDataRoot.mkdirs();
		}
		this.spFile = new File(this.fileRoot, "server.json");
	}
	
	public File getUserDataFile(UUID uuid) {
		return new File(this.userDataRoot, uuid.toString() + ".json");
	}
	
	public Json getServerProperties() throws IOException {
		Json data;
		if (!spFile.exists()) {
			// default config values
			data = Json.object()
				.set("tcp-port", 3636)
				.set("max-players", 120)
				.set("server-name", "An Arkanoid Server")
			;
			Files.writeString(spFile.toPath(), data.toString());
			return data;
		}
		// read the properties
		return Json.read(Files.readString(spFile.toPath()));
 	}
	
	// for the leaderboard bullshitty
	private List<PlayerDetails> allPlayersDetails;
	private long lastAllPlayersFetch = 0;
	
	public synchronized List<PlayerDetails> getAllPlayerDetails() throws IOException {
		File[] files = this.userDataRoot.listFiles();
		if (files == null) return Collections.emptyList(); // what the fuck
		
		// this can blow up the FS if called like a bazillion times/s, so cap it
		if (System.currentTimeMillis() - lastAllPlayersFetch >= 10_000) {
			if (this.allPlayersDetails == null) {
				this.allPlayersDetails = new ArrayList<>(files.length);
			}
			this.allPlayersDetails.clear();
		} else {
			return this.allPlayersDetails;
		}
		
		for (File file : files) {
			if (!file.getName().endsWith(".json")) continue;
			Json player = Json.read(Files.readString(file.toPath()));
			allPlayersDetails.add(new PlayerDetails(
				player.at(PlayerData.NAME).asString(), 
				!player.has(PlayerData.TOTAL_WINS) ? 0 : player.at(PlayerData.TOTAL_WINS).asInteger(),
				!player.has(PlayerData.HIGHSCORE_SOLO) ? 0 : player.at(PlayerData.HIGHSCORE_SOLO).asInteger()
			));
		}
		
		this.lastAllPlayersFetch = System.currentTimeMillis();
		return allPlayersDetails;
	}
	
}
